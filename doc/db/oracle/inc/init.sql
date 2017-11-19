-包括创建的表，实始化的数据

 begin

P_DEL_GNMK('xtgl_bhgzzd');
P_DEL_GNMK('xtgl_bhgzck');
P_DEL_GNMK('xtgl_bhdcz');
P_DEL_GNMK('xtgl_bhdck');
--P_DEL_ML('系统管理','编号管理');
  
 P_ADD_ML('系统管理','编号管理');
 P_ADD_GNMK('系统管理~编号管理','xtgl_bhgzzd','编号规则制定','../numberPages/jsp/numRuleInput.jsp','090000');        
 P_ADD_GNMK('系统管理~编号管理','xtgl_bhgzck','编号规则查看','../numberPages/jsp/numRuleList.jsp','090000'); 
 P_ADD_GNMK('系统管理~编号管理','xtgl_bhdcz','编号段注册','../numberPages/jsp/numSegmentInput.jsp','090000'); 
 P_ADD_GNMK('系统管理~编号管理','xtgl_bhdck','编号段查看','../numberPages/jsp/numSegmentList.jsp','090000');
      
END;
/

 


