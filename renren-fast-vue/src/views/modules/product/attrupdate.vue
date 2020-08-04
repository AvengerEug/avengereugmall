<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card">
          <el-tabs tab-position="left" style="width:98%">
            <el-tab-pane
              :label="group.attrGroupName"
              v-for="(group,gidx) in dataResp.attrGroups"
              :key="group.attrGroupId"
            >
              <!-- 遍历属性,每个tab-pane对应一个表单，每个属性是一个表单项  spu.baseAttrs[0] = [{attrId:xx,val:}]-->
              <el-form ref="form" :model="dataResp">
                <el-form-item
                  :label="attr.attrName"
                  v-for="(attr,aidx) in group.attrs"
                  :key="attr.attrId"
                >
                  <!-- {{ "【" + dataResp.baseAttrs[gidx][aidx].attrValues + "】"}} -->
                  <el-input
                    v-model="dataResp.baseAttrs[gidx][aidx].attrId"
                    type="hidden"
                    v-show="false"
                  ></el-input>
                  
                  <el-select
                    v-model="dataResp.baseAttrs[gidx][aidx].attrValues"
                    :multiple="attr.valueType === 1"
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请选择或输入值"
                  >
                    <el-option
                      v-for="(val,vidx) in attr.valueSelect.split(';')"
                      :key="vidx"
                      :label="val"
                      :value="val"
                    ></el-option>
                  </el-select>
                  <el-checkbox
                    v-model="dataResp.baseAttrs[gidx][aidx].showDesc"
                    :true-label="1"
                    :false-label="0"
                  >快速展示</el-checkbox>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
          <div style="margin:auto">
            <el-button type="success" style="float:right" @click="submitSpuAttrs">确认修改</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  components: {},
  props: {},
  data() {
    return {
      spuId: "",
      catelogId: "",
      dataResp: {
        //后台返回的所有数据
        attrGroups: [],
        baseAttrs: []
      },
      spuAttrsMap: {}
    };
  },
  computed: {},
  methods: {
    clearData(){
      this.dataResp.attrGroups = [];
      this.dataResp.baseAttrs = [];
      this.spuAttrsMap = {};
    },
    getSpuBaseAttrs() {
      this.$http({
        url: this.$http.adornUrl(`/product/attr/base/listforspu/${this.spuId}`),
        method: "get"
      }).then(({ data }) => {
        data.data.forEach(item => {
          this.spuAttrsMap["" + item.attrId] = item;
        });
        console.log("spu的基础属性~~~~", this.spuAttrsMap);
      });
    },
    getQueryParams() {
      this.spuId = this.$route.query.spuId;
      this.catelogId = this.$route.query.catelogId;
      console.log("----", this.spuId, this.catelogId);
    },
    showBaseAttrs() {
      let _this = this;
      this.$http({
        url: this.$http.adornUrl(
          `/product/attrgroup/${this.catelogId}/withattr`
        ),
        method: "get",
        params: this.$http.adornParams({})
      }).then(({ data }) => {
        //先对表单的baseAttrs进行初始化，回显
        data.data.forEach(item => {
          const attrArray = []
          item.attrs.forEach(attr => {
            let v = "";
            if (_this.spuAttrsMap["" + attr.attrId]) {
              v = _this.spuAttrsMap["" + attr.attrId].attrValue.split(";");
              if (v.length == 1) {
                v = v[0].trim();
              }
            }

            const obj = {
              attrId: attr.attrId,
              attrName: attr.attrName,
              attrValues: attr.valueType === 1 ? (v.length ? [].concat(v) : []) : v,
              showDesc: _this.spuAttrsMap["" + attr.attrId]
                ? _this.spuAttrsMap["" + attr.attrId].quickShow
                : attr.showDesc
            }

            attrArray.push(obj);
          });
          _this.dataResp.baseAttrs.push(attrArray);
        });
        _this.dataResp.attrGroups = data.data;
        console.log('this.dataResp :>> ', this.dataResp);
      });
    },
    submitSpuAttrs() {
      console.log("·····", this.dataResp.baseAttrs);
      //spu_id  attr_id  attr_name             attr_value             attr_sort  quick_show
      let submitData = [];
      this.dataResp.baseAttrs.forEach(item => {
        item.forEach(attr => {
          let val = "";
          if (attr.attrValues instanceof Array) {
            val = attr.attrValues.join(";");
          } else {
            val = attr.attrValues;
          }

          if (val != "") {
            submitData.push({
              attrId: attr.attrId,
              attrName: attr.attrName,
              attrValue: val,
              quickShow: attr.showDesc
            });
          }
        });
      });

      this.$confirm("修改商品规格信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl(`/product/attr/update/${this.spuId}`),
            method: "put",
            data: this.$http.adornData(submitData, false)
          }).then(({ data }) => {
            this.$message({
              type: "success",
              message: "属性修改成功!"
            });
          });
        })
        .catch((e) => {
          this.$message({
            type: "info",
            message: "已取消修改"+e
          });
        });
    }
  },
  created() {},
  activated() {
    this.clearData();
    this.getQueryParams();
    if (this.spuId && this.catelogId) {
      this.getSpuBaseAttrs();
      this.showBaseAttrs();
    }
  }
};
</script>
<style scoped>
</style>