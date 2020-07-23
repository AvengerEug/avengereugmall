<template>
  <div>
    <el-tree
      :data='menus'
      :props='defaultProps'
      :expand-on-click-node='false'
      show-checkbox
      node-key='catId'
      :default-expanded-keys="expandedKeys"
      :draggable="true"
      :allow-drop="allowDrop"
    >
      <!-- 
        第一、二级分类下，显示append按钮
        第三级分类下，显示delete按钮
      -->
      <span class='custom-tree-node' slot-scope='{ node, data }'>
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if='data.catLevel <= 2'
            type='text'
            size='mini'
            @click='() => append(data)'
          >Append</el-button>
          <el-button
            v-if='data.children.length == 0'
            type='text'
            size='mini'
            @click='() => remove(node, data)'
          >Delete</el-button>
          <el-button
            type='text'
            size='mini'
            @click='edit(data)'
          >Edit</el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog title='商品分类' :visible.sync='dialogFormVisible' :close-on-click-modal="false">
      <el-form ref="categoryForm" :model='categoryForm' :rules='rules'>
        <el-form-item label='分类名称' :label-width='formLabelWidth' prop='name'>
          <el-input v-model='categoryForm.name' autocomplete='off' />
        </el-form-item>
        <el-form-item label='分类图标' :label-width='formLabelWidth' prop='icon'>
          <el-input v-model='categoryForm.icon' autocomplete='off' />
        </el-form-item>
        <el-form-item label='商品单位' :label-width='formLabelWidth' prop='productUnit'>
          <el-input v-model='categoryForm.productUnit' autocomplete='off' />
        </el-form-item>
      </el-form>
      <div slot='footer' class='dialog-footer'>
        <el-button @click='disabledDialog()'>取 消</el-button>
        <el-button type='primary' @click='handleCategory()'>确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
let id = 1000

const defaultCategoryForm = {
  catId: null,
  name: '',
  parentCid: '', // 程序获取
  catLevel: '', // 程序获取
  showStatus: 1, // 默认值
  sort: 0, // 默认值
  icon: '',
  productUnit: '',
  productCount: '',
}

export default {
  data() {
    return {
      menus: [],
      expandedKeys: [],
      // 数据中的哪个字段作为children，哪个字段作为label显示
      defaultProps: {
        children: 'children',
        label: 'name',
      },
      categoryForm: defaultCategoryForm,
      rules: {
        name: [
          { required: true, message: '请输入商品分类名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ]
      },
      dialogFormVisible: false,
      formLabelWidth: '120px',
    }
  },
  methods: {
    allowDrop (draggingNode, dropNode, type) {
      // 被拖动的当前节点以及所在父节点总层数不能大于3
      // 拖动的当前节点: draggingNode
      // 拖动的目标节点: dropNode
      // 位于目标节点的位置: type

      // 1. 假设draggingNode在dropNode的上边 ===> draggingNode节点的层数 + dropNode父节点的层数  不能大于3
      // 2. 假设draggingNode在dropNode的里边 ===> draggingNode节点的层数 + dropNode父节点的层数 不能大于3

      console.log('arguments :>> ', arguments)
      if (draggingNode.level === 3 || dropNode.level === 3) {
        return false
      }

      if (draggingNode.level + dropNode.level > 3) {
        return false
      }


      return true
    },
    disabledDialog () {
      this.dialogFormVisible = false
    },
    showDialog () {
      this.dialogFormVisible = true
    },
    resetCategoryForm() {
      this.categoryForm = defaultCategoryForm
    },
    append (data) {
      // 防止点了更新，再次点击新增时，用的是同一个category对象
      this.resetCategoryForm()

      this.categoryForm.parentCid = data.catId
      this.categoryForm.catLevel = data.catLevel + 1
      this.showDialog()
    },
    edit (data) {
      // 此处应该是发送请求回显最新的数据
      // 假设一直停留在分类管理页面，此时要修改当前分类，但是这个分类在5分钟前被其他管理员修改过了，
      // 此时应该回显最新的数据，应该call api回显
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: 'get'
      }).then(( {data} ) => {
        this.categoryForm = data.category
        this.showDialog()
      })

    },
    handleCategory () {
      this.$refs['categoryForm'].validate((valid) => {
        if (valid) {
          if (this.categoryForm.catId) {
            // 更新
            this.updateCategory()
          } else {
            this.saveCategory()
          }
        } else {
          this.$message.error('请按规定填写信息')
          return false;
        }
      });
    },
    updateCategory () {
      // 只更新这几个字段，后面的catLevel、sort将使用拖拽的形式来呈现
      let { catId, name, icon, productUnit } = this.categoryForm
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'put',
        data: {
          catId: catId,
          name: name,
          icon: icon,
          productUnit: productUnit
        }
      }).then(( {data} ) => {
        this.$message({
          type: 'success',
          message: '更新成功',
        })
        this.disabledDialog()
        this.expandedKeys = [this.categoryForm.catId]
        this.getDataList()
      })
    },
    saveCategory () {
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.categoryForm
      }).then(( {data} ) => {
        this.$message({
          type: 'success',
          message: '新增成功',
        })
        this.disabledDialog()
        this.expandedKeys = [data.category.catId]
        this.getDataList()
      })
    },
    remove(node, data) {
      this.$confirm(`此操作将删除 【${data.name}】 类别, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
          this.deleteCategoryByIds(data.catId).then(() => {
            this.$message({
              type: 'success',
              message: '删除成功',
            })

            const parent = node.parent
            const children = parent.data.children || parent.data
            const index = children.findIndex((d) => d.catId === data.catId)
            children.splice(index, 1)
          })
        })
        .catch(() => {
          console.log('取消操作')
        })
    },
    deleteCategoryByIds(ids) {
      return this.$http({
        url: this.$http.adornUrl('/product/category/delete'),
        method: 'delete',
        data: [ids],
      })
    },
    getDataList() {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get',
      }).then(({ data }) => {
        console.log('成功获取到菜单数据...', data)
        this.menus = data.data
      })
    },
  },
  created() {
    this.getDataList()
  },
}
</script>

<style>
</style>