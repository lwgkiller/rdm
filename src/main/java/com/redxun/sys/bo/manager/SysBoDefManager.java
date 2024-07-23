
package com.redxun.sys.bo.manager;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.bpm.core.manager.BoDataUtil;
import com.redxun.bpm.core.manager.IFormDataHandler;
import com.redxun.bpm.form.entity.BpmFormView;
import com.redxun.bpm.form.entity.OpinionDef;
import com.redxun.bpm.form.manager.BpmFormViewManager;
import com.redxun.core.dao.IDao;
import com.redxun.core.dao.mybatis.BaseMybatisDao;
import com.redxun.core.database.api.ITableOperator;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.ExtBaseManager;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.ITenant;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.bo.dao.SysBoDefDao;
import com.redxun.sys.bo.dao.SysBoRelationDao;
import com.redxun.sys.bo.entity.SysBoAttr;
import com.redxun.sys.bo.entity.SysBoDef;
import com.redxun.sys.bo.entity.SysBoEnt;
import com.redxun.sys.bo.entity.SysBoRelation;

/**
 * 
 * <pre> 
 * 描述：BO定义 处理接口
 * 作者:ray
 * 日期:2017-02-15 15:02:18
 * 版权：广州红迅软件
 * </pre>
 */
@Service
public class SysBoDefManager extends ExtBaseManager<SysBoDef>{
	
	@Resource
	private SysBoDefDao sysBoDefDao;
	@Resource
	private ITableOperator  tableOperator;
	@Resource
	private SysBoEntManager sysBoEntManager;
	@Resource
	private SysBoRelationDao sysBoRelationDao;
	@Resource
	private BpmFormViewManager bpmFormViewManager;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected IDao getDao() {
		return sysBoDefDao;
	}
	
	@Override
	public BaseMybatisDao getMyBatisDao() {
		return sysBoDefDao;
	}
	
	
	/**
	 * 在保存BO定义之前先检查bo能否添加。
	 * <pre>
	 *  需要检查的点
	 *  1.BO定义系统中是否存在。
	 *  	存在则提示
	 *  2.实体系统中是否存在。
	 *  	存在则提示
	 *  3.物理表检查。
	 *  	如果bo定义需要生成表的情况。
	 *  	
	 *  	判断boDefId是否为空
	 *  	3.1如果为空表示第一次添加，需要判断物理表是否存在，如果存在则不能添加。
	 *  		存在提示。
	 *  	3.2如果不为空的情况，还需要判断 genTable 状态位判断
	 *  		如果原来状态为no，现在为yes。
	 *  			则表示需要添加物理表,可以走3.1逻辑。
	 *  		如果原来状态为yes。
	 *  
	 *  		则表示已经添加过物理表，还需要判断是否产生了新的物理表，还需要走判断。
	 *  		则需要检查对应的物理表是否已经存在数据。
	 *  		1.如果不存在则直接删除表，进行重建。
	 *  		2.如果存在数据则不对数据库进行操作，但是对字段进行标记是否生成，用户可以在界面进行操作。
	 *  
	 * </pre>
	 * @param boDefId
	 * @param ent
	 * @return
	 */
	public JsonResult canExeSave(String boDefId, SysBoEnt ent,String tenantId){
		JsonResult result=new JsonResult(true);
		List<SysBoEnt> ents= sysBoEntManager.getListByBoEnt(ent,false);
		//添加表
		SysBoEnt mainEnt=sysBoEntManager.getMain(ents);
		
		//1.判断定义是否存在
		result= checkBoDefExist( boDefId, mainEnt.getName(), tenantId);
		if(!result.isSuccess()) return result;
		//2.检查实体是否存在
		result= sysBoEntManager.isEntExist(ents);
		if(!result.isSuccess()) return result;
		
		//生成物理表的情况。
		if(SysBoDef.BO_YES.equals( ent.getGenTable())){
			//直接生成的情况
			if(StringUtil.isEmpty(boDefId)){
				result= sysBoEntManager.isTableExist(ents);
			}
			else{
				SysBoDef boDef=this.get(boDefId);
				if(boDef.getSupportDb().equals(SysBoDef.BO_NO)){
					result= sysBoEntManager.isTableExist(ents);
				}
				else{
					List<SysBoEnt> newEnts=new ArrayList<SysBoEnt>();
					for(SysBoEnt boent:ents){
						if(boent.getVersion().equals(SysBoDef.VERSION_NEW)){
							newEnts.add(boent);
						}
					}
					result= sysBoEntManager.isTableExist(newEnts);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 检查bo定义。
	 * @param boDefId
	 * @param alias
	 * @param tenantId
	 * @return
	 */
	public JsonResult checkBoDefExist(String boDefId,String alias,String tenantId){
		JsonResult result=new JsonResult(true);
		
		boolean rtn= sysBoDefDao.getCountByAlias(alias, tenantId, boDefId);
		if(rtn){
			result.setSuccess(false);
			result.setMessage("bo定义" + alias +"已经存在");
		}
		return result;
	}
	
	/**
	 * 根据别名获取bo定义。
	 * @param alias
	 * @return
	 */
	public SysBoDef getByAlias(String alias){
		String tenantId=ContextUtil.getCurrentTenantId();
		SysBoDef boDef= sysBoDefDao.getByAlias(alias, tenantId);
		if(boDef==null){
			 boDef= sysBoDefDao.getByAlias(alias, ITenant.ADMIN_TENANT_ID);
		}
		return boDef;
	}
	
	 
	
	
	/**
	 * 保存实体数据。
	 * <pre>
	 *  boDefId 
	 *  1.如果为空表示实体是新增的。
	 *  	如果为新增则需要检查是否系统中是否已存在实体定义。
	 *  	不存在才可以添加。
	 *  	
	 *  2.如果不为空表示实体需要更新。
	 *  	实体的处理可以保留BO定义数据，实体数据先删除掉，再重新构建bo数据。
	 *  	
	 * 关于物理表的处理
	 *  boDefId 为空的情况。
	 * 	1.还需要检测系统中物理表是否存在，如果存在则不能添加。
	 *  2.如果没有问题可以直接添加表。
	 *  
	 *  不为空的情况，表示bo已经保存过。
	 *  
	 *  	但是可能还没有生成物理表。
	 *  	可以参考boDefId为空的情况处理。
	 *  
	 *  	如果已经生成物理表。
	 *  	检测表中是否有数据
	 *  	
	 *  	1.如果没有数据，则删除表重建。
	 *  	2.如果有数据，则提示用户是否增加列。
	 *  	
	 *  
	 * 	
	 *  
	 * </pre>
	 * @param boDefId	bo定义ID
	 * @param ent		需要保存的实体。
	 * @return
	 * @throws SQLException 
	 */
	public String  saveBoEnt(String boDefId, SysBoEnt ent,Map<String,List<SysBoAttr>> sysBoAttrs,String tenantId,List<OpinionDef> opinionDefs) throws SQLException{
		//是否生成物理表。
		boolean genTable=ent.getGenTable().equals(SysBoDef.BO_YES);
		List<SysBoEnt> ents= sysBoEntManager.getListByBoEnt(ent,false);
		//添加表
		SysBoEnt mainEnt=sysBoEntManager.getMain(ents);
		List<SysBoEnt> subEnts=sysBoEntManager.getSub(ents);
		
		String jsonOpinion=JSONArray.toJSONString(opinionDefs);
		
		//表示新增。
		if(StringUtil.isEmpty(boDefId)){
			if(genTable){
				sysBoEntManager.createTable(ent);
				for(SysBoEnt sysBoEnt:subEnts){
					sysBoEnt.setGenTable(SysBoDef.BO_YES);
				}
			}
			//添加BODEF
			boDefId=IdUtil.getId();
			SysBoDef boDef=new SysBoDef();
			boDef.setId(boDefId);
			boDef.setAlais(ent.getName());
			boDef.setName(ent.getComment());
			boDef.setGenMode(SysBoDef.GEN_MODE_FORM);
			//boDef.setTreeId(ent.getTreeId()); //modify by Louis
			boDef.setTreeId(ent.getCategoryId());
			boDef.setSupportDb(ent.getGenTable());
			boDef.setOpinionDef(jsonOpinion);
			
			sysBoDefDao.create(boDef);
			
			resetEntVersion(ent);
			
			saveEnt(boDefId,mainEnt,subEnts,true);
		}
		else{
			handTable(boDefId,ent,sysBoAttrs);
			
			//根据bo定义删除
			removeByBoDefId(boDefId);

			if(genTable){
				for(SysBoEnt sysBoEnt:subEnts){
					sysBoEnt.setGenTable(SysBoDef.BO_YES);
				}
			}
			saveEnt(boDefId,mainEnt,subEnts,false);
			//设置BO定义。
			SysBoDef boDef=sysBoDefDao.get(boDefId);
			boDef.setName(ent.getComment());
			//boDef.setTreeId(ent.getTreeId()); //modify by louis
			boDef.setTreeId(ent.getCategoryId());
			boDef.setOpinionDef(jsonOpinion);
			boDef.setSupportDb(ent.getGenTable());
			sysBoDefDao.update(boDef);
			
		}
		return boDefId;
		
	}
	
	private void handTable(String boDefId,SysBoEnt ent,Map<String,List<SysBoAttr>> sysBoAttrMap) throws SQLException{
		boolean genTable=ent.getGenTable().equals(SysBoDef.BO_YES);
		if(!genTable) return;
		SysBoDef boDef=sysBoDefDao.get(boDefId);
		//未生成物理表。
		if(boDef.getSupportDb().equals(SysBoDef.BO_NO)){
			sysBoEntManager.createTable(ent);
			resetEntVersion(ent);
			return;
		}
		//已生成物理表。
		//判断物理表是否存在数据。
		SysBoEnt oldEnt = sysBoEntManager.get(ent.getId());
		String tableName=oldEnt.getTableName();
		ent.setTableName(tableName);
		boolean hasData=tableOperator.hasData(tableName);
		//主表没有数据的情况，直接删除表重建。
		if(!hasData){
			rebuidTable(ent);
			resetEntVersion(ent);
			return;
		}
		//存在数据
		List<SysBoEnt> list= sysBoEntManager.getListByBoEnt(ent,false);
		for(SysBoEnt boEnt:list){
			List<SysBoAttr> sysBoAttrs=sysBoAttrMap.get(boEnt.getId());
			//创建的新表。
			if(SysBoDef.VERSION_NEW.equals( boEnt.getVersion())){
				sysBoEntManager.createSingleTable(boEnt);
				resetEntVersion(boEnt);
			}
			//表已存在,这个时候不对表进行操作，在列属性中记录变更情况。
			else{
				//处理新增的列
				List<SysBoAttr> attrs= boEnt.getSysBoAttrs();
				for(SysBoAttr attr:attrs){

					if(attr.getStatus().equals(SysBoDef.VERSION_NEW)){
						//新增的列。
						sysBoEntManager.createColumn(boEnt.getTableName(), attr);
						attr.setStatus(SysBoDef.VERSION_BASE);
					}else if((attr.getStatus().equals(SysBoDef.VERSION_DIFF))){//如果有区别
						//处理int和varchar的增大
						for (SysBoAttr sysBoAttr : sysBoAttrs) {
							if(SysBoAttr.DATATYPE_CLOB.equals(sysBoAttr.getDataType()) || attr==null){continue;}
							if(attr.getFieldName().equals(sysBoAttr.getFieldName())){
								//处理类型为clob，length为null
								if(attr.getLength()==null){
									attr.setLength(0);
								}
								if(sysBoAttr.getLength()==null){
									sysBoAttr.setLength(0);
								}
								int disLength=attr.getLength()-sysBoAttr.getLength();//修改后的长度比原先的长度差
								attr.setOriginName(attr.getName());
								if((attr.getDataType().equals(sysBoAttr.getDataType())||(attr.getDataType().equals("varchar")&&sysBoAttr.getDataType().equals("number")))&&disLength>=0){//如果数据类型一样才允许修改数据库
									sysBoEntManager.updateColumn(boEnt.getTableName(), attr);	
								}
								if(attr.getIsSingle() == 0 && sysBoAttr.getIsSingle() == 1) { //单属性转成多属性 ,创建额外一列
									SysBoAttr extAttr = new SysBoAttr();
									extAttr.setId(IdUtil.getId());
									extAttr.setFieldName(sysBoAttr.getFieldName() + "_NAME");
									extAttr.setName(sysBoAttr.getName() + "_NAME");
									extAttr.setDataType("varchar");
									extAttr.setLength(attr.getLength());
									extAttr.setIsSingle(1);
									extAttr.setTenantId(ContextUtil.getCurrentTenantId());
									sysBoEntManager.createColumn(boEnt.getTableName(), extAttr);
								} else if(attr.getIsSingle() == 1 && sysBoAttr.getIsSingle() == 0) {
									String delFieldName = sysBoAttr.getFieldName() + "_NAME";
									ProcessHandleHelper.addErrorMsg("-- 表" + boEnt.getTableName() + "中的" + sysBoAttr.getFieldName() + ", 由多属性转成了单属性，建议执行以下sql语句");
									ProcessHandleHelper.addErrorMsg("ALTER FROM " + boEnt.getTableName().toUpperCase() + " DROP COLUMN " + delFieldName);
								}
							}
						}
						attr.setStatus(SysBoDef.VERSION_BASE);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 重置boEnt的状态。
	 * @param boEnt
	 */
	private void resetEntVersion(SysBoEnt boEnt){
		resetEntSingleVersion(boEnt);
		List<SysBoEnt> list=boEnt.getBoEntList();
		if(BeanUtil.isEmpty(list)) return;
		for(SysBoEnt ent:list){
			resetEntSingleVersion(ent);
		}
	}
	
	/**
	 * 重置单个实体的状态。
	 * @param boEnt
	 */
	private void resetEntSingleVersion(SysBoEnt boEnt){
		boEnt.setVersion(SysBoDef.VERSION_BASE);
		for(SysBoAttr attr:boEnt.getSysBoAttrs()){
			attr.setStatus(SysBoDef.VERSION_BASE);
		}
	}
	
	/**
	 * 重建表。
	 * <pre>
	 * 1.先删除所有的表。
	 * 2.在重新创建所有的表。
	 * </pre>
	 * @param ent
	 * @throws SQLException
	 */
	private void rebuidTable(SysBoEnt ent) throws SQLException{
		//String tablePre= DbUtil.getTablePre();
		List<SysBoEnt> ents=new ArrayList<SysBoEnt>();
		ents.add(ent);
		ents.addAll(ent.getBoEntList());
		for(SysBoEnt boEnt:ents){
			//关联表不处理。
			if(boEnt.getIsRef()==1) continue;
			String tableName=boEnt.getTableName();
			tableOperator.dropTable(tableName);
		}
		sysBoEntManager.createTable(ent);
		
	}
	
	/**
	 * 保存ent 源数据和映射关系。
	 * @param boDefId
	 * @param mainEnt
	 * @param subEnts
	 */
	public void saveEnt(String boDefId,SysBoEnt mainEnt,List<SysBoEnt> subEnts,boolean genId){
		String tenantId=ContextUtil.getCurrentTenantId();
		if(sysBoEntManager.getByName(mainEnt.getName()) == null) {
			sysBoEntManager.createBoEnt(mainEnt,genId);
			createRel(boDefId, mainEnt);
		}
		
		//添加子实体
		for(SysBoEnt subEnt:subEnts){
			//增加关联引用实体。
			if(subEnt.getIsRef()==1){
				BpmFormView bpmFormView= bpmFormViewManager.getLatestByKey(subEnt.getFormAlias(), tenantId);
				String refBoDefId=bpmFormView.getBoDefId();
				SysBoEnt boEnt= sysBoEntManager.getByBoDefId(refBoDefId);
				boEnt.setRelationType(subEnt.getRelationType());
				boEnt.setIsRef(1);
				boEnt.setFormAlias(subEnt.getFormAlias());
				createRel(boDefId, boEnt);
			}
			else{
				if(sysBoEntManager.getByName(subEnt.getName()) == null) {
					subEnt.setCategoryId(mainEnt.getCategoryId());
					sysBoEntManager.createBoEnt(subEnt,genId);
					createRel(boDefId, subEnt);
				}
				
			}
			
		}
	}
	
	/**
	 * 创建实体映射关系。
	 * @param boDefId
	 * @param ent
	 */
	private void createRel(String boDefId, SysBoEnt ent){
		SysBoRelation rel=new SysBoRelation();
		rel.setId(IdUtil.getId());
		rel.setBoDefid(boDefId);
		rel.setBoEntid(ent.getId());
		rel.setRelationType(ent.getRelationType());
		
		rel.setIsRef(ent.getIsRef());
		rel.setFormAlias(ent.getFormAlias());
		
		sysBoRelationDao.create(rel);
	}
	
	private void createRel(String boDefId, String entId,String relationType){
		SysBoRelation rel=new SysBoRelation();
		rel.setId(IdUtil.getId());
		rel.setBoDefid(boDefId);
		rel.setBoEntid(entId);
		rel.setRelationType(relationType);
		
		rel.setIsRef(0);
		
		sysBoRelationDao.create(rel);
	}
	
	
	/**
	 * 根据boDefId 删除数据。
	 * @param boDefId
	 */
	private void removeByBoDefId(String boDefId){
		List<String> entIds= getEntIdByBoDefId( boDefId);
		for(String entId:entIds){
			//根据entID 删除实体ID
			sysBoEntManager.removeByEntId(entId);
		}
		//删除关联关系
		sysBoRelationDao.delByMainId(boDefId);
	}
	
	/**
	 * 获取关联的ENTID
	 * @param boDefId
	 * @return
	 */
	private List<String> getEntIdByBoDefId(String boDefId){
		List<SysBoRelation> list= sysBoRelationDao.getByBoDefId(boDefId);
		List<String> ents=new ArrayList<String>();
		for(SysBoRelation ent :list){
			if(ent.getIsRef()==1) continue;
			ents.add(ent.getBoEntid());
		}
		return ents;
	}
	
	public SysBoRelation getEntByEntNameAndDefId(String boDefId,String entName){
		return sysBoRelationDao.getByBoDefIdAndEntName(boDefId, entName);
	}
	
	/**
	 * 删除bo定义。
	 * @param boDefId
	 * @throws SQLException
	 */
	public void removeBoDef(String boDefId) throws SQLException{
		//判断是否有实体
		List<SysBoRelation> list= sysBoRelationDao.getByBoDefId(boDefId);
		if(BeanUtil.isNotEmpty(list)) {
			//删除表。
			dropTable(boDefId);
			//删除bo定义。
			removeByBoDefId(boDefId);
		}
		//删除bo定义
		this.delete(boDefId);
		
	}
	
	/**
	 * 删除物理表。
	 * @param boDefId
	 * @throws SQLException
	 */
	private void dropTable(String boDefId) throws SQLException{
		//清除表
		SysBoEnt ent= sysBoEntManager.getByBoDefId(boDefId, false);
		if(!SysBoDef.BO_YES.equals(ent.getGenTable())) return;
		
		String tableName= ent.getTableName();
		tableOperator.dropTable(tableName);
		
		List<SysBoEnt> subEnts=ent.getBoEntList();
		for(SysBoEnt subEnt:subEnts){
			if(subEnt.getIsRef()==1) continue;
			tableOperator.dropTable(subEnt.getTableName());
		}
	}
	
	/**
	 * 保存sysbodef对象。
	 * @param jsonObj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public JsonResult save(JSONObject jsonObj) throws IllegalAccessException, InvocationTargetException{
		JsonResult result=new JsonResult(true);
		String msg="";
		SysBoDef sysBoDef= JSONObject.toJavaObject(jsonObj, SysBoDef.class);
		String tenantId=ContextUtil.getCurrentTenantId();
		//检查BODEF是否存在。
		result=checkBoDefExist(sysBoDef.getId(), sysBoDef.getAlais(), tenantId);
		if(!result.isSuccess()) return result;
		
		sysBoDef.setGenMode(SysBoDef.GEN_MODE_CREATE);
		JSONArray relations=jsonObj.getJSONArray("rows");
		String mainId=jsonObj.getString("mainEntId");
		if(StringUtil.isEmpty(sysBoDef.getId())){
			
			String boDefId=IdUtil.getId();
			sysBoDef.setId(boDefId);
			create(sysBoDef);
			addRelation(boDefId, mainId, relations);
			msg =  "BO定义成功创建!";
			
			result.setMessage(msg);
		}
		else{
			//检查是否关联了表单
			List<BpmFormView> formViews= bpmFormViewManager.getByBoId(sysBoDef.getId());
			if(BeanUtil.isNotEmpty(formViews)) {
				result.setSuccess(false);
				result.setMessage("已经关联了表单,请先删除表单再进行修改!");
				return result;
			}
			String boDefId=sysBoDef.getId();
			
			SysBoDef orignDef=this.get(boDefId);
			//将新的数据复制到原来的数据上来。
			BeanUtil.copyNotNullProperties(orignDef,sysBoDef);
			update(orignDef);
			//删除关系
			sysBoRelationDao.delByMainId(boDefId);
			addRelation(boDefId, mainId, relations);
			
			msg =  "BO定义成功更新!";
			result.setMessage(msg);
		}
		return result;
	}
	
	
	
	
	private void addRelation(String boDefId,String mainId,JSONArray relations){
		//主表
		createRel(boDefId, mainId, "main");
		//子表
		if(BeanUtil.isNotEmpty(relations)){
			for(int i=0;i<relations.size();i++){
				JSONObject subJson=relations.getJSONObject(i);
				createRel(boDefId, subJson.getString("entId"),subJson.getString("relateType"));
			}
		}
	}
	
	/**
	 * 根据bodefId 删除关系，删除
	 * @param boDefIds
	 */
	public JsonResult removeByDefId(String boDefIds){
		if(StringUtil.isEmpty(boDefIds))return new JsonResult(false,"无BO定义删除!");
		String[] defIds = boDefIds.split(",");
		StringBuffer sb = new StringBuffer();
		JsonResult result=new JsonResult(true);
		for (String boDefId:defIds) {
			if(StringUtil.isEmpty(boDefId))continue;
			SysBoDef def = sysBoDefDao.get(boDefId);	
			List<BpmFormView> formViews= bpmFormViewManager.getByBoId(boDefId);
			if(BeanUtil.isNotEmpty(formViews))  {
				sb.append("["+def.getName()+"]关联了表单!\n");
				result.setSuccess(false);
				result.setMessage("删除失败!");
			}else {
				//删除关联关系
				sysBoRelationDao.delByMainId(boDefId);
				//删除定义
				sysBoDefDao.delete(boDefId);
				sb.append("["+def.getName()+"]删除成功!\n");
			}
		}
		result.setData(sb.toString());
		
		return result;
	}
	
	
	/**
	 * 根据boDefId 和主键获取 JSON数据。
	 * @param boDefId
	 * @param pk
	 * @return
	 */
	public JSONObject getDataByBoDef(String boDefId,String pk){
		IFormDataHandler handler=BoDataUtil.getDataHandler(ProcessConfig.DATA_SAVE_MODE_DB);
		JSONObject json=  handler.getData(boDefId, pk);
		JSONObject returnJson = new JSONObject();
		//main
		JSONObject main=new JSONObject();
		JSONObject sub=new JSONObject();
		for(String key :json.keySet()){
			if(!key.startsWith("SUB_")) {
				main.put(key, json.get(key));
			}
			else{
				sub.put(key.replaceFirst("SUB_", ""), json.get(key));
			}
			
		}
		returnJson.put("main", main);
		returnJson.put("sub", sub);
		return returnJson;
	}
	
	/**
	 * 根据ID获取获取别名。
	 * @param boDefId
	 * @return
	 */
	public String getAliasById(String boDefId){
		SysBoDef def=sysBoDefDao.get(boDefId);
		return def.getAlais();
		
	}
}
