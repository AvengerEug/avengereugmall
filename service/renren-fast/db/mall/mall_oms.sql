CREATE database if NOT EXISTS `mall_oms` default character set utf8mb4 collate utf8mb4_unicode_ci;
USE `mall_oms`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists oms_order;

drop table if exists oms_order_item;

drop table if exists oms_order_operate_history;

drop table if exists oms_order_return_apply;

drop table if exists oms_order_return_reason;

drop table if exists oms_order_setting;

drop table if exists oms_payment_info;

drop table if exists oms_refund_info;

/*==============================================================*/
/* Table: oms_order                                             */
/*==============================================================*/
create table oms_order
(
   id                   bigint not null auto_increment comment 'id',
   member_id            bigint comment 'member_id',
   order_sn             char(32) comment '������',
   coupon_id            bigint comment 'ʹ�õ��Ż�ȯ',
   create_time          datetime comment 'create_time',
   member_username      varchar(200) comment '�û���',
   total_amount         decimal(18,4) comment '�����ܶ�',
   pay_amount           decimal(18,4) comment 'Ӧ���ܶ�',
   freight_amount       decimal(18,4) comment '�˷ѽ��',
   promotion_amount     decimal(18,4) comment '�����Ż��������ۡ����������ݼۣ�',
   integration_amount   decimal(18,4) comment '���ֵֿ۽��',
   coupon_amount        decimal(18,4) comment '�Ż�ȯ�ֿ۽��',
   discount_amount      decimal(18,4) comment '��̨��������ʹ�õ��ۿ۽��',
   pay_type             tinyint comment '֧����ʽ��1->֧������2->΢�ţ�3->������ 4->���������',
   source_type          tinyint comment '������Դ[0->PC������1->app����]',
   status               tinyint comment '����״̬��0->�����1->��������2->�ѷ�����3->����ɣ�4->�ѹرգ�5->��Ч������',
   delivery_company     varchar(64) comment '������˾(���ͷ�ʽ)',
   delivery_sn          varchar(64) comment '��������',
   auto_confirm_day     int comment '�Զ�ȷ��ʱ�䣨�죩',
   integration          int comment '���Ի�õĻ���',
   growth               int comment '���Ի�õĳɳ�ֵ',
   bill_type            tinyint comment '��Ʊ����[0->������Ʊ��1->���ӷ�Ʊ��2->ֽ�ʷ�Ʊ]',
   bill_header          varchar(255) comment '��Ʊ̧ͷ',
   bill_content         varchar(255) comment '��Ʊ����',
   bill_receiver_phone  varchar(32) comment '��Ʊ�˵绰',
   bill_receiver_email  varchar(64) comment '��Ʊ������',
   receiver_name        varchar(100) comment '�ջ�������',
   receiver_phone       varchar(32) comment '�ջ��˵绰',
   receiver_post_code   varchar(32) comment '�ջ����ʱ�',
   receiver_province    varchar(32) comment 'ʡ��/ֱϽ��',
   receiver_city        varchar(32) comment '����',
   receiver_region      varchar(32) comment '��',
   receiver_detail_address varchar(200) comment '��ϸ��ַ',
   note                 varchar(500) comment '������ע',
   confirm_status       tinyint comment 'ȷ���ջ�״̬[0->δȷ�ϣ�1->��ȷ��]',
   delete_status        tinyint comment 'ɾ��״̬��0->δɾ����1->��ɾ����',
   use_integration      int comment '�µ�ʱʹ�õĻ���',
   payment_time         datetime comment '֧��ʱ��',
   delivery_time        datetime comment '����ʱ��',
   receive_time         datetime comment 'ȷ���ջ�ʱ��',
   comment_time         datetime comment '����ʱ��',
   modify_time          datetime comment '�޸�ʱ��',
   primary key (id)
);

alter table oms_order comment '����';

/*==============================================================*/
/* Table: oms_order_item                                        */
/*==============================================================*/
create table oms_order_item
(
   id                   bigint not null auto_increment comment 'id',
   order_id             bigint comment 'order_id',
   order_sn             char(32) comment 'order_sn',
   spu_id               bigint comment 'spu_id',
   spu_name             varchar(255) comment 'spu_name',
   spu_pic              varchar(500) comment 'spu_pic',
   spu_brand            varchar(200) comment 'Ʒ��',
   category_id          bigint comment '��Ʒ����id',
   sku_id               bigint comment '��Ʒsku���',
   sku_name             varchar(255) comment '��Ʒsku����',
   sku_pic              varchar(500) comment '��ƷskuͼƬ',
   sku_price            decimal(18,4) comment '��Ʒsku�۸�',
   sku_quantity         int comment '��Ʒ���������',
   sku_attrs_vals       varchar(500) comment '��Ʒ����������ϣ�JSON��',
   promotion_amount     decimal(18,4) comment '��Ʒ�����ֽ���',
   coupon_amount        decimal(18,4) comment '�Ż�ȯ�Żݷֽ���',
   integration_amount   decimal(18,4) comment '�����Żݷֽ���',
   real_amount          decimal(18,4) comment '����Ʒ�����Żݺ�ķֽ���',
   gift_integration     int comment '���ͻ���',
   gift_growth          int comment '���ͳɳ�ֵ',
   primary key (id)
);

alter table oms_order_item comment '��������Ϣ';

/*==============================================================*/
/* Table: oms_order_operate_history                             */
/*==============================================================*/
create table oms_order_operate_history
(
   id                   bigint not null auto_increment comment 'id',
   order_id             bigint comment '����id',
   operate_man          varchar(100) comment '������[�û���ϵͳ����̨����Ա]',
   create_time          datetime comment '����ʱ��',
   order_status         tinyint comment '����״̬��0->�����1->��������2->�ѷ�����3->����ɣ�4->�ѹرգ�5->��Ч������',
   note                 varchar(500) comment '��ע',
   primary key (id)
);

alter table oms_order_operate_history comment '����������ʷ��¼';

/*==============================================================*/
/* Table: oms_order_return_apply                                */
/*==============================================================*/
create table oms_order_return_apply
(
   id                   bigint not null auto_increment comment 'id',
   order_id             bigint comment 'order_id',
   sku_id               bigint comment '�˻���Ʒid',
   order_sn             char(32) comment '�������',
   create_time          datetime comment '����ʱ��',
   member_username      varchar(64) comment '��Ա�û���',
   return_amount        decimal(18,4) comment '�˿���',
   return_name          varchar(100) comment '�˻�������',
   return_phone         varchar(20) comment '�˻��˵绰',
   status               tinyint(1) comment '����״̬[0->������1->�˻��У�2->����ɣ�3->�Ѿܾ�]',
   handle_time          datetime comment '����ʱ��',
   sku_img              varchar(500) comment '��ƷͼƬ',
   sku_name             varchar(200) comment '��Ʒ����',
   sku_brand            varchar(200) comment '��ƷƷ��',
   sku_attrs_vals       varchar(500) comment '��Ʒ��������(JSON)',
   sku_count            int comment '�˻�����',
   sku_price            decimal(18,4) comment '��Ʒ����',
   sku_real_price       decimal(18,4) comment '��Ʒʵ��֧������',
   reason               varchar(200) comment 'ԭ��',
   description��         varchar(500) comment '����',
   desc_pics            varchar(2000) comment 'ƾ֤ͼƬ���Զ��Ÿ���',
   handle_note          varchar(500) comment '����ע',
   handle_man           varchar(200) comment '������Ա',
   receive_man          varchar(100) comment '�ջ���',
   receive_time         datetime comment '�ջ�ʱ��',
   receive_note         varchar(500) comment '�ջ���ע',
   receive_phone        varchar(20) comment '�ջ��绰',
   company_address      varchar(500) comment '��˾�ջ���ַ',
   primary key (id)
);

alter table oms_order_return_apply comment '�����˻�����';

/*==============================================================*/
/* Table: oms_order_return_reason                               */
/*==============================================================*/
create table oms_order_return_reason
(
   id                   bigint not null auto_increment comment 'id',
   name                 varchar(200) comment '�˻�ԭ����',
   sort                 int comment '����',
   status               tinyint(1) comment '����״̬',
   create_time          datetime comment 'create_time',
   primary key (id)
);

alter table oms_order_return_reason comment '�˻�ԭ��';

/*==============================================================*/
/* Table: oms_order_setting                                     */
/*==============================================================*/
create table oms_order_setting
(
   id                   bigint not null auto_increment comment 'id',
   flash_order_overtime int comment '��ɱ������ʱ�ر�ʱ��(��)',
   normal_order_overtime int comment '����������ʱʱ��(��)',
   confirm_overtime     int comment '�������Զ�ȷ���ջ�ʱ�䣨�죩',
   finish_overtime      int comment '�Զ���ɽ���ʱ�䣬���������˻����죩',
   comment_overtime     int comment '������ɺ��Զ�����ʱ�䣨�죩',
   member_level         tinyint(2) comment '��Ա�ȼ���0-���޻�Ա�ȼ���ȫ��ͨ�ã�����-��Ӧ��������Ա�ȼ���',
   primary key (id)
);

alter table oms_order_setting comment '����������Ϣ';

/*==============================================================*/
/* Table: oms_payment_info                                      */
/*==============================================================*/
create table oms_payment_info
(
   id                   bigint not null auto_increment comment 'id',
   order_sn             char(32) comment '�����ţ�����ҵ��ţ�',
   order_id             bigint comment '����id',
   alipay_trade_no      varchar(50) comment '֧����������ˮ��',
   total_amount         decimal(18,4) comment '֧���ܽ��',
   subject              varchar(200) comment '��������',
   payment_status       varchar(20) comment '֧��״̬',
   create_time          datetime comment '����ʱ��',
   confirm_time         datetime comment 'ȷ��ʱ��',
   callback_content     varchar(4000) comment '�ص�����',
   callback_time        datetime comment '�ص�ʱ��',
   primary key (id)
);

alter table oms_payment_info comment '֧����Ϣ��';

/*==============================================================*/
/* Table: oms_refund_info                                       */
/*==============================================================*/
create table oms_refund_info
(
   id                   bigint not null auto_increment comment 'id',
   order_return_id      bigint comment '�˿�Ķ���',
   refund               decimal(18,4) comment '�˿���',
   refund_sn            varchar(64) comment '�˿����ˮ��',
   refund_status        tinyint(1) comment '�˿�״̬',
   refund_channel       tinyint comment '�˿�����[1-֧������2-΢�ţ�3-������4-���]',
   refund_content       varchar(5000),
   primary key (id)
);

alter table oms_refund_info comment '�˿���Ϣ';
