package com.perceptivesoftware.renderservice.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * MBean for {@link RenderServiceConfiguration}.
 */
public class ConfigurationMBean implements DynamicMBean {

	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		return RenderServiceConfiguration.getInstance().getString(attribute, "");
	}

	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		return;
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList result = new AttributeList();

		for (String attribute : attributes) {
			result.add(new Attribute(attribute, RenderServiceConfiguration.getInstance().getString(
					attribute, "")));
		}

		return result;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		Map<String, String> parameters = RenderServiceConfiguration.getInstance().getParameters();

		List<MBeanAttributeInfo> attributeList = new ArrayList<MBeanAttributeInfo>();

		for (Entry<String, String> parameter : parameters.entrySet()) {
			MBeanAttributeInfo info =
					new MBeanAttributeInfo(parameter.getKey(), String.class.getCanonicalName(),
							parameter.getKey(), true, false, false);
			attributeList.add(info);
		}

		MBeanAttributeInfo[] attributes =
				attributeList.toArray(new MBeanAttributeInfo[attributeList.size()]);

		MBeanConstructorInfo[] constructors;
		try {
			constructors =
					new MBeanConstructorInfo[] { new MBeanConstructorInfo("Default constructor",
							getClass().getConstructor(String.class)), };
		} catch (NoSuchMethodException e) {
			constructors = new MBeanConstructorInfo[0];
		}

		MBeanOperationInfo[] operations = new MBeanOperationInfo[0];
		MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[0];

		MBeanInfo info =
				new MBeanInfo(getClass().getCanonicalName(), "Configuration properties",
						attributes, constructors, operations, notifications);

		return info;
	}

}
