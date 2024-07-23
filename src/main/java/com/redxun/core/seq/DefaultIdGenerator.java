package com.redxun.core.seq;

import com.baidu.fsg.uid.UidGenerator;

import javax.annotation.Resource;

/**
 * 默认的ID产生类
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn）
 * 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class DefaultIdGenerator implements IdGenerator {

    @Resource
    private UidGenerator uidGenerator;

    @Override
    public Long getLID() {
        return uidGenerator.getUID();
    }

    @Override
    public String getSID() {
        return String.valueOf( uidGenerator.getUID());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

//    public JdbcTemplate jdbcTemplate;
//    /**
//     * 是否为UUID产生的ID作为主键
//     */
//    private boolean isUseGuid=false;
//
//	public boolean isUseGuid() {
//		return isUseGuid;
//	}
//
//	public void setUseGuid(boolean isUseGuid) {
//		this.isUseGuid = isUseGuid;
//	}
//
//	/**
//     * 增长段值
//     */
//    private final Long increaseBound = 10000l;
//    /**
//     * 当前DBID
//     */
//    private Long dbid = 1l;
//    /**
//     * 当前递增的最大值
//     */
//    private Long maxDbid = -1l;
//    /**
//     * 机器ID
//     */
//    private Long machineDbid = 1l;
//    /**
//     * 机器名称 多台物理机器集群部署时，需要唯一区分
//     */
//    private String machineName;
//
//    public void setMachineName(String machineName_){
//    	this.machineName=machineName_;
//    }
//
//    /**
//     * 获取唯一的ID标识
//     *
//     * @return
//     */
//    public synchronized Long getLID() {
//        if (dbid >= maxDbid) {
//            genNextDbIds();
//        }
//        Long nextId = dbid++;
//        return nextId + machineDbid * 10000000000000L;
//    }
//
//    /**
//     * 获取唯一主键字符串
//     * @return
//     */
//    public synchronized String getSID(){
//    	if(isUseGuid){
//    		return UUID.randomUUID().toString();
//    	}else{
//    		return getLID().toString();
//    	}
//    }
//
//    public void genNextDbIds() {
//        String sql = "UPDATE MI_DB_ID SET START_=?,MAX_=? WHERE ID_=?";
//        dbid = maxDbid;
//        maxDbid += increaseBound;
//        jdbcTemplate.update(sql, dbid, maxDbid, machineDbid);
//    }
//
//    @SuppressWarnings("unchecked")
//    public void afterPropertiesSet() throws Exception {
//    	String tmpName="";
//    	if(StringUtil.isEmpty(machineName)){
//    		InetAddress addr = InetAddress.getLocalHost();
//    		tmpName=addr.getHostName();
//    	}
//    	else{
//    		tmpName=this.machineName;
//    	}
//
//        String sql = "select * from MI_DB_ID where MAC_NAME_=?";
//        //检查该机器是否已经存在增长的键值记录
//        try {
//            jdbcTemplate.queryForObject(sql, new RowMapper() {
//                public Object mapRow(ResultSet rs, int i) throws SQLException {
//                    dbid = rs.getLong("START_");
//                    maxDbid = rs.getLong("MAX_");
//                    machineDbid = rs.getLong("ID_");
//                    return machineDbid;
//                }
//            }, tmpName);
//            //插入该机器的键值增长记录
//            genNextDbIds();
//        } catch (Exception ex) {
//            String maxSql = "select max(ID_) from MI_DB_ID";
//            Number maxNum = jdbcTemplate.queryForObject(maxSql, Integer.class);
//            Integer maxResult=null;
//            if(maxNum==null){
//            	maxResult=1;
//            }else{
//            	maxResult=maxNum.intValue();
//            }
//            if (maxResult == 0) {
//                maxResult = 1;
//            } else {
//                maxResult++;
//            }
//            machineDbid = new Long(maxResult);
//            maxDbid = dbid + increaseBound;
//            sql = "INSERT INTO MI_DB_ID(ID_,START_,MAX_,MAC_NAME_)VALUES(?,?,?,?)";
//            jdbcTemplate.update(sql, new Object[]{machineDbid, dbid, maxDbid, tmpName});
//        }
//    }
//
//    public Long getDbid() {
//        return dbid;
//    }
//
//    public void setDbid(Long dbid) {
//        this.dbid = dbid;
//    }
//
//    public Long getMaxDbid() {
//        return maxDbid;
//    }
//
//    public void setMaxDbid(Long maxDbid) {
//        this.maxDbid = maxDbid;
//    }
//
//    public Long getMachineDbid() {
//        return machineDbid;
//    }
//
//    public void setMachineDbid(Long machineDbid) {
//        this.machineDbid = machineDbid;
//    }
//
//
//
//    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

}
