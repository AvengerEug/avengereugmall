CREATE database if NOT EXISTS `mall_pms` default character set utf8mb4 collate utf8mb4_unicode_ci;
USE `mall_pms`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists pms_attr;

drop table if exists pms_attr_attrgroup_relation;

drop table if exists pms_attr_group;

drop table if exists pms_brand;

drop table if exists pms_category;

drop table if exists pms_category_brand_relation;

drop table if exists pms_comment_replay;

drop table if exists pms_product_attr_value;

drop table if exists pms_sku_images;

drop table if exists pms_sku_info;

drop table if exists pms_sku_sale_attr_value;

drop table if exists pms_spu_comment;

drop table if exists pms_spu_images;

drop table if exists pms_spu_info;

drop table if exists pms_spu_info_desc;

/*==============================================================*/
/* Table: pms_attr                                              */
/*==============================================================*/
create table pms_attr
(
   attr_id              bigint not null auto_increment comment '����id',
   attr_name            char(30) comment '������',
   search_type          tinyint comment '�Ƿ���Ҫ����[0-����Ҫ��1-��Ҫ]',
   icon                 varchar(255) comment '����ͼ��',
   value_select         char(255) comment '��ѡֵ�б�[�ö��ŷָ�]',
   attr_type            tinyint comment '��������[0-�������ԣ�1-�������ԣ�2-���������������ǻ�������]',
   enable               bigint comment '����״̬[0 - ���ã�1 - ����]',
   catelog_id           bigint comment '��������',
   show_desc            tinyint comment '����չʾ���Ƿ�չʾ�ڽ����ϣ�0-�� 1-�ǡ�����sku����Ȼ���Ե���',
   primary key (attr_id)
);

alter table pms_attr comment '��Ʒ����';

/*==============================================================*/
/* Table: pms_attr_attrgroup_relation                           */
/*==============================================================*/
create table pms_attr_attrgroup_relation
(
   id                   bigint not null auto_increment comment 'id',
   attr_id              bigint comment '����id',
   attr_group_id        bigint comment '���Է���id',
   attr_sort            int comment '������������',
   primary key (id)
);

alter table pms_attr_attrgroup_relation comment '����&���Է������';

/*==============================================================*/
/* Table: pms_attr_group                                        */
/*==============================================================*/
create table pms_attr_group
(
   attr_group_id        bigint not null auto_increment comment '����id',
   attr_group_name      char(20) comment '����',
   sort                 int comment '����',
   descript             varchar(255) comment '����',
   icon                 varchar(255) comment '��ͼ��',
   catelog_id           bigint comment '��������id',
   primary key (attr_group_id)
);

alter table pms_attr_group comment '���Է���';

/*==============================================================*/
/* Table: pms_brand                                             */
/*==============================================================*/
create table pms_brand
(
   brand_id             bigint not null auto_increment comment 'Ʒ��id',
   name                 char(50) comment 'Ʒ����',
   logo                 varchar(2000) comment 'Ʒ��logo��ַ',
   descript             longtext comment '����',
   show_status          tinyint comment '��ʾ״̬[0-����ʾ��1-��ʾ]',
   first_letter         char(1) comment '��������ĸ',
   sort                 int comment '����',
   primary key (brand_id)
);

alter table pms_brand comment 'Ʒ��';

/*==============================================================*/
/* Table: pms_category                                          */
/*==============================================================*/
create table pms_category
(
   cat_id               bigint not null auto_increment comment '����id',
   name                 char(50) comment '��������',
   parent_cid           bigint comment '������id',
   cat_level            int comment '�㼶',
   show_status          tinyint comment '�Ƿ���ʾ[0-����ʾ��1��ʾ]',
   sort                 int comment '����',
   icon                 char(255) comment 'ͼ���ַ',
   product_unit         char(50) comment '������λ',
   product_count        int comment '��Ʒ����',
   primary key (cat_id)
);

alter table pms_category comment '��Ʒ��������';

/*==============================================================*/
/* Table: pms_category_brand_relation                           */
/*==============================================================*/
create table pms_category_brand_relation
(
   id                   bigint not null auto_increment,
   brand_id             bigint comment 'Ʒ��id',
   catelog_id           bigint comment '����id',
   brand_name           varchar(255),
   catelog_name         varchar(255),
   primary key (id)
);

alter table pms_category_brand_relation comment 'Ʒ�Ʒ������';

/*==============================================================*/
/* Table: pms_comment_replay                                    */
/*==============================================================*/
create table pms_comment_replay
(
   id                   bigint not null auto_increment comment 'id',
   comment_id           bigint comment '����id',
   reply_id             bigint comment '�ظ�id',
   primary key (id)
);

alter table pms_comment_replay comment '��Ʒ���ۻظ���ϵ';

/*==============================================================*/
/* Table: pms_product_attr_value                                */
/*==============================================================*/
create table pms_product_attr_value
(
   id                   bigint not null auto_increment comment 'id',
   spu_id               bigint comment '��Ʒid',
   attr_id              bigint comment '����id',
   attr_name            varchar(200) comment '������',
   attr_value           varchar(200) comment '����ֵ',
   attr_sort            int comment '˳��',
   quick_show           tinyint comment '����չʾ���Ƿ�չʾ�ڽ����ϣ�0-�� 1-�ǡ�',
   primary key (id)
);

alter table pms_product_attr_value comment 'spu����ֵ';

/*==============================================================*/
/* Table: pms_sku_images                                        */
/*==============================================================*/
create table pms_sku_images
(
   id                   bigint not null auto_increment comment 'id',
   sku_id               bigint comment 'sku_id',
   img_url              varchar(255) comment 'ͼƬ��ַ',
   img_sort             int comment '����',
   default_img          int comment 'Ĭ��ͼ[0 - ����Ĭ��ͼ��1 - ��Ĭ��ͼ]',
   primary key (id)
);

alter table pms_sku_images comment 'skuͼƬ';

/*==============================================================*/
/* Table: pms_sku_info                                          */
/*==============================================================*/
create table pms_sku_info
(
   sku_id               bigint not null auto_increment comment 'skuId',
   spu_id               bigint comment 'spuId',
   sku_name             varchar(255) comment 'sku����',
   sku_desc             varchar(2000) comment 'sku��������',
   catalog_id           bigint comment '��������id',
   brand_id             bigint comment 'Ʒ��id',
   sku_default_img      varchar(255) comment 'Ĭ��ͼƬ',
   sku_title            varchar(255) comment '����',
   sku_subtitle         varchar(2000) comment '������',
   price                decimal(18,4) comment '�۸�',
   sale_count           bigint comment '����',
   primary key (sku_id)
);

alter table pms_sku_info comment 'sku��Ϣ';

/*==============================================================*/
/* Table: pms_sku_sale_attr_value                               */
/*==============================================================*/
create table pms_sku_sale_attr_value
(
   id                   bigint not null auto_increment comment 'id',
   sku_id               bigint comment 'sku_id',
   attr_id              bigint comment 'attr_id',
   attr_name            varchar(200) comment '����������',
   attr_value           varchar(200) comment '��������ֵ',
   attr_sort            int comment '˳��',
   primary key (id)
);

alter table pms_sku_sale_attr_value comment 'sku��������&ֵ';

/*==============================================================*/
/* Table: pms_spu_comment                                       */
/*==============================================================*/
create table pms_spu_comment
(
   id                   bigint not null auto_increment comment 'id',
   sku_id               bigint comment 'sku_id',
   spu_id               bigint comment 'spu_id',
   spu_name             varchar(255) comment '��Ʒ����',
   member_nick_name     varchar(255) comment '��Ա�ǳ�',
   star                 tinyint(1) comment '�Ǽ�',
   member_ip            varchar(64) comment '��Աip',
   create_time          datetime comment '����ʱ��',
   show_status          tinyint(1) comment '��ʾ״̬[0-����ʾ��1-��ʾ]',
   spu_attributes       varchar(255) comment '����ʱ�������',
   likes_count          int comment '������',
   reply_count          int comment '�ظ���',
   resources            varchar(1000) comment '����ͼƬ/��Ƶ[json���ݣ�[{type:�ļ�����,url:��Դ·��}]]',
   content              text comment '����',
   member_icon          varchar(255) comment '�û�ͷ��',
   comment_type         tinyint comment '��������[0 - ����Ʒ��ֱ�����ۣ�1 - �����۵Ļظ�]',
   primary key (id)
);

alter table pms_spu_comment comment '��Ʒ����';

/*==============================================================*/
/* Table: pms_spu_images                                        */
/*==============================================================*/
create table pms_spu_images
(
   id                   bigint not null auto_increment comment 'id',
   spu_id               bigint comment 'spu_id',
   img_name             varchar(200) comment 'ͼƬ��',
   img_url              varchar(255) comment 'ͼƬ��ַ',
   img_sort             int comment '˳��',
   default_img          tinyint comment '�Ƿ�Ĭ��ͼ',
   primary key (id)
);

alter table pms_spu_images comment 'spuͼƬ';

/*==============================================================*/
/* Table: pms_spu_info                                          */
/*==============================================================*/
create table pms_spu_info
(
   id                   bigint not null auto_increment comment '��Ʒid',
   spu_name             varchar(200) comment '��Ʒ����',
   spu_description      varchar(1000) comment '��Ʒ����',
   catalog_id           bigint comment '��������id',
   brand_id             bigint comment 'Ʒ��id',
   weight               decimal(18,4),
   publish_status       tinyint comment '�ϼ�״̬[0 - �¼ܣ�1 - �ϼ�]',
   create_time          datetime,
   update_time          datetime,
   primary key (id)
);

alter table pms_spu_info comment 'spu��Ϣ';

/*==============================================================*/
/* Table: pms_spu_info_desc                                     */
/*==============================================================*/
create table pms_spu_info_desc
(
   spu_id               bigint not null comment '��Ʒid',
   decript              longtext comment '��Ʒ����',
   primary key (spu_id)
);

alter table pms_spu_info_desc comment 'spu��Ϣ����';
