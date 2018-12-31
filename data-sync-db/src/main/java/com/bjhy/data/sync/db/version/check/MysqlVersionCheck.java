package com.bjhy.data.sync.db.version.check;

import java.util.ArrayList;
import java.util.List;

import com.bjhy.data.sync.db.domain.AddColumnAttribute;
import com.bjhy.data.sync.db.domain.SyncLogicEntity;
import com.bjhy.data.sync.db.inter.face.OwnInterface.VersionCheck;

/**
 * oracle的版本检测实现
 * @author wubo
 *
 */
public class MysqlVersionCheck implements VersionCheck{
	
	private SyncLogicEntity syncLogicEntity;
	
	public MysqlVersionCheck(SyncLogicEntity syncLogicEntity) {
		super();
		this.syncLogicEntity = syncLogicEntity;
	}

	@Override
	public List<String> getAddVersionCheckColumnSql(List<AddColumnAttribute> addToTableColumns) {
		List<String> alterSqlList = new ArrayList<String>();
		for (AddColumnAttribute addColumnAttribute : addToTableColumns) {
			String alterSql = "alter table "+addColumnAttribute.getTableName()+" add ("+addColumnAttribute.getColumnName()+" varchar("+addColumnAttribute.getLength()+"))";
			alterSqlList.add(alterSql);
		}
		return alterSqlList;
	}

	@Override
	public String getToDeleteByVersionCheck() {
		String syncVersionCheck = VersionCheckCore.SYNC_VERSION_CHECK;
		String toTableName = syncLogicEntity.getSingleStepSyncConfig().getToTableName();
		String toDeleteSql = "DELETE FROM "+toTableName+" WHERE "+syncVersionCheck+" in(:"+syncVersionCheck+") or "+syncVersionCheck+" is null or "+syncVersionCheck+"=''";
		return toDeleteSql;
	}
}

