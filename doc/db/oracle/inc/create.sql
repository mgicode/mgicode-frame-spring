
CREATE TABLE zcgl.xt_num_rule ( 
     id  VARCHAR2  (32)    PRIMARY KEY , 
    code  VARCHAR2  (50)    UNIQUE , 
    name  VARCHAR2  (200) , 
    sys_name  VARCHAR2  (200) , 
    sys_code  VARCHAR2  (200) , 
    rule  VARCHAR2  (1000) , 
    make_time  DATE , 
    make_user  VARCHAR2  (100) , 
    description  VARCHAR2  (1000) , 
    rule_len  NUMBER (22), 
    create_time  DATE , 
    creator  VARCHAR2  (500)  
)   ;  
 Create sequence  SEQ_xt_num_rule_ID  
 INCREMENT BY 1 START WITH 1  NOMAXvalue   NOCYCLE  nocache; 


CREATE TABLE zcgl.xt_num_segment ( 
     id  VARCHAR2  (32)    PRIMARY KEY , 
    seg_code  VARCHAR2  (50)    UNIQUE , 
    seg_name  VARCHAR2  (200) , 
    seg_limits  NUMBER (22), 
    system_code  VARCHAR2  (50) , 
    system_name  VARCHAR2  (100) , 
    seg_clz_name  VARCHAR2  (100) , 
    seg_url  VARCHAR2  (100) , 
    seg_content  VARCHAR2  (1000) , 
    sup_format  VARCHAR2  (100) , 
    sup_format_desc  VARCHAR2  (1000) , 
    seg_len  NUMBER (22), 
    creator  VARCHAR2  (50) , 
    create_time  DATE , 
    description  VARCHAR2  (1000)  
)   ;  
 Create sequence  SEQ_xt_num_segment_ID  
 INCREMENT BY 1 START WITH 1  NOMAXvalue   NOCYCLE  nocache; 
 
  
 