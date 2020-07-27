CREATE database if NOT EXISTS `mall_ums` default character set utf8mb4 collate utf8mb4_unicode_ci;
USE `mall_ums`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


drop table if exists wms_purchase;

drop table if exists wms_purchase_detail;

drop table if exists wms_ware_info;

drop table if exists wms_ware_order_task;

drop table if exists wms_ware_order_task_detail;

drop table if exists wms_ware_sku;

/*==============================================================*/
/* Table: wms_purchase                                          */
/*==============================================================*/
create table wms_purchase
(
   id                   bigint not null auto_increment comment '�ɹ���id',
   assignee_id          bigint comment '�ɹ���id',
   assignee_name        varchar(255) comment '�ɹ�����',
   phone                char(13) comment '��ϵ��ʽ',
   priority             int(4) comment '���ȼ�',
   status               int(4) comment '״̬',
   ware_id              bigint comment '�ֿ�id',
   amount               decimal(18,4) comment '�ܽ��',
   create_time          datetime comment '��������',
   update_time          datetime comment '��������',
   primary key (id)
);

alter table wms_purchase comment '�ɹ���Ϣ';

/*==============================================================*/
/* Table: wms_purchase_detail                                   */
/*==============================================================*/
create table wms_purchase_detail
(
   id                   bigint not null auto_increment,
   purchase_id          bigint comment '�ɹ���id',
   sku_id               bigint comment '�ɹ���Ʒid',
   sku_num              int comment '�ɹ�����',
   sku_price            decimal(18,4) comment '�ɹ����',
   ware_id              bigint comment '�ֿ�id',
   status               int comment '״̬[0�½���1�ѷ��䣬2���ڲɹ���3����ɣ�4�ɹ�ʧ��]',
   primary key (id)
);

/*==============================================================*/
/* Table: wms_ware_info                                         */
/*==============================================================*/
create table wms_ware_info
(
   id                   bigint not null auto_increment comment 'id',
   name                 varchar(255) comment '�ֿ���',
   address              varchar(255) comment '�ֿ��ַ',
   areacode             varchar(20) comment '�������',
   primary key (id)
);

alter table wms_ware_info comment '�ֿ���Ϣ';

/*==============================================================*/
/* Table: wms_ware_order_task                                   */
/*==============================================================*/
create table wms_ware_order_task
(
   id                   bigint not null auto_increment comment 'id',
   order_id             bigint comment 'order_id',
   order_sn             varchar(255) comment 'order_sn',
   consignee            varchar(100) comment '�ջ���',
   consignee_tel        char(15) comment '�ջ��˵绰',
   delivery_address     varchar(500) comment '���͵�ַ',
   order_comment        varchar(200) comment '������ע',
   payment_way          tinyint(1) comment '���ʽ�� 1:���߸��� 2:�������',
   task_status          tinyint(2) comment '����״̬',
   order_body           varchar(255) comment '��������',
   tracking_no          char(30) comment '��������',
   create_time          datetime comment 'create_time',
   ware_id              bigint comment '�ֿ�id',
   task_comment         varchar(500) comment '��������ע',
   primary key (id)
);

alter table wms_ware_order_task comment '��湤����';

/*==============================================================*/
/* Table: wms_ware_order_task_detail                            */
/*==============================================================*/
create table wms_ware_order_task_detail
(
   id                   bigint not null auto_increment comment 'id',
   sku_id               bigint comment 'sku_id',
   sku_name             varchar(255) comment 'sku_name',
   sku_num              int comment '�������',
   task_id              bigint comment '������id',
   primary key (id)
);

alter table wms_ware_order_task_detail comment '��湤����';

/*==============================================================*/
/* Table: wms_ware_sku                                          */
/*==============================================================*/
create table wms_ware_sku
(
   id                   bigint not null auto_increment comment 'id',
   sku_id               bigint comment 'sku_id',
   ware_id              bigint comment '�ֿ�id',
   stock                int comment '�����',
   sku_name             varchar(200) comment 'sku_name',
   stock_locked         int comment '�������',
   primary key (id)
);

alter table wms_ware_sku comment '��Ʒ���';
