package com.taobao.pamirs.cache.management;

import java.util.Set;

import org.jgroups.Address;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@ManagedResource
@Component
public class ExcludeHosts
{
	private Set<Address>	list;

	@ManagedAttribute
	public Set<Address> getList()
	{
		return list;
	}

	@ManagedAttribute
	public void setList(final Set<Address> list)
	{
		this.list = list;
	}

	@ManagedOperation
	public void addMember(final @ManagedOperationParameter(description = "增加主机", name = "host") Address host)
	{
		list.add(host);
	}

	@ManagedOperation
	public void removeMember(
	        final @ManagedOperationParameter(description = "增加主机", name = "host") Address host)
	{
		list.remove(host);
	}

}
