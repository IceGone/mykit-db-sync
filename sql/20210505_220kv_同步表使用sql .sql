SET FOREIGN_KEY_CHECKS =0;

create or replace view syn_server_status
as select ID,CREATETIME,UPDATETIME,DOWNTIME,ISLIVE from zyc_stlf_nw.syn_server_status;

create or replace view syn_job_conf
as select JOBID,JOBNAME,CRON,SRCSQL,SRCTABLEFIELDS,DESTTABLE,DESTTABLEFIELDS,DESTTABLEKEY,DESTTABLEUPDATE,SYNCOUNT from zyc_stlf_nw.syn_job_conf;

DROP TABLE IF EXISTS base_substation_syn;
CREATE TABLE base_substation_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	STATIONID INT(11) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (STATIONID),
	KEY INDEX_UID_07 (STATIONID),
	KEY INDEX__ESS_07 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_07 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS base_bus_syn;
CREATE TABLE base_bus_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID),
	KEY INDEX_UID_08 (BUSID),
	KEY INDEX__ESS_08 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_08 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS base_acline_syn;
CREATE TABLE base_acline_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	ID INT(11) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (ID),
	KEY INDEX_UID_09 (ID),
	KEY INDEX__ESS_09 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_09 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS base_unit_syn;
CREATE TABLE base_unit_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	ID INT(11) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (ID),
	KEY INDEX_UID_10 (ID),
	KEY INDEX__ESS_10 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_10 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS base_transformer_syn;
CREATE TABLE base_transformer_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	ID INT(11) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (ID),
	KEY INDEX_UID_11 (ID),
	KEY INDEX__ESS_11 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_11 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS lf_his_96lc_base_syn;
CREATE TABLE lf_his_96lc_base_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_12 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_12 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_12 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS lf_his_96lc_syn;
CREATE TABLE lf_his_96lc_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_13 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_13 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_13 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS lf_q_his_96lc_syn;
CREATE TABLE lf_q_his_96lc_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_14 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_14 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_14 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS bus_load_repair_log_syn;
CREATE TABLE bus_load_repair_log_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    HISDATE VARCHAR(8) NOT NULL,
    HISTIME VARCHAR(4) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,HISDATE,HISTIME),
	KEY INDEX_UID_15 (BUSID,HISDATE,HISTIME),
	KEY INDEX__ESS_15 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_15 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS bus_fc_96lc_submit_syn;
CREATE TABLE bus_fc_96lc_submit_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_16 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_16 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_16 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS bus_fc_q_96lc_submit_syn;
CREATE TABLE bus_fc_q_96lc_submit_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_17 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_17 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_17 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS bus_fc_96lc_submit_modify_syn;
CREATE TABLE bus_fc_96lc_submit_modify_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	BUSID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (BUSID,CALIBERID,YMD),
	KEY INDEX_UID_18 (BUSID,CALIBERID,YMD),
	KEY INDEX__ESS_18 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_18 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS bus_fc_96lc_syn;
CREATE TABLE bus_fc_96lc_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	FCNO INT(11) NOT NULL,
    YMD VARCHAR(6) NOT NULL,
    METHODID VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (FCNO,YMD,METHODID),
	KEY INDEX_UID_19 (FCNO,YMD,METHODID),
	KEY INDEX__ESS_19 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_19 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

DROP TABLE IF EXISTS lf_net_his_96lc_syn;
CREATE TABLE lf_net_his_96lc_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	NETID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (NETID,CALIBERID,YMD),
	KEY INDEX_UID_20 (NETID,CALIBERID,YMD),
	KEY INDEX__ESS_20 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_20 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

-- 视图同步表
DROP TABLE IF EXISTS lf_net_fc_96lc_syn;
CREATE TABLE lf_net_fc_96lc_syn (
	SJCID INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '自增ID，每条新增记录自增',
	JOBID INT(11) NOT NULL COMMENT '关联SYN_JOB_CONF表的JOBID 获取同步表名',
	NETID INT(11) NOT NULL,
    CALIBERID VARCHAR(6) NOT NULL,
    YMD VARCHAR(8) NOT NULL,
	CREATETIME timestamp NOT NULL COMMENT '创建时间',
	SYNTIME timestamp NULL COMMENT '同步完成时间',
	ENV INT(2) NOT NULL COMMENT '主调->备调(0),备调->主调(1)',
	OPEARATE INT(2) NOT NULL COMMENT 'insert or update(0),delete(1)',
	SYNCOUNT INT(2) DEFAULT 1 COMMENT '需要同步次数，每次修改所需同步表(如base_bus)时此值加1，避免同步时又对所需同步表作修改造成的部分数据缺失',
	SYNSTATUS INT(2) NOT NULL COMMENT '同步状态(0：未同步；1：开始同步；2：完成同步)',
	PRIMARY KEY ( SJCID ) ,
	UNIQUE (NETID,CALIBERID,YMD),
	KEY INDEX_UID_21 (NETID,CALIBERID,YMD),
	KEY INDEX__ESS_21 (ENV,SYNCOUNT,SYNSTATUS),
	KEY INDEX_EOSS_21 (ENV,OPEARATE,SYNCOUNT,SYNSTATUS),
	FOREIGN KEY (JOBID) REFERENCES syn_job_conf(JOBID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='主备数据同步状态表，每个需要同步的表对应一个';

-- INSERT INTO `syn_server_status`(`id`, `createtime`, `updatetime`, `downtime`, `islive`) VALUES (1, '2020-10-31 09:03:00', '2020-10-31 09:03:00', NULL, 0);

-- 对需要同步的表新增字段
-- UPDATE lf_his_96lc SET LASTTIME ='2020-04-26 09:03:00' WHERE YMD ='20210101';
alter table base_substation add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_07 ON base_substation(LASTTIME);

alter table base_bus add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_08 ON base_bus(LASTTIME);

alter table base_acline add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_09 ON base_acline(LASTTIME);

alter table base_unit add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_10 ON base_unit(LASTTIME);

alter table base_transformer add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_11 ON base_transformer(LASTTIME);

alter table lf_his_96lc_base add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_12 ON lf_his_96lc_base(LASTTIME);

alter table lf_his_96lc add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_13 ON lf_his_96lc(LASTTIME);

alter table lf_q_his_96lc add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_14 ON lf_q_his_96lc(LASTTIME);

alter table BUS_LOAD_REPAIR_LOG add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_15 ON BUS_LOAD_REPAIR_LOG(LASTTIME);

alter table BUS_FC_96LC_SUBMIT add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_16 ON BUS_FC_96LC_SUBMIT(LASTTIME);

alter table BUS_FC_Q_96LC_SUBMIT add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_17 ON BUS_FC_Q_96LC_SUBMIT(LASTTIME);

alter table BUS_FC_96LC_SUBMIT_MODIFY add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_18 ON BUS_FC_96LC_SUBMIT_MODIFY(LASTTIME);

alter table BUS_FC_96LC add column LASTTIME timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间';
CREATE INDEX index_lt_19 ON BUS_FC_96LC(LASTTIME);

SET FOREIGN_KEY_CHECKS =1;