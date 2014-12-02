package com.psd.rendering.openrender.oorender.module;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {
	
	private static Logger log = Logger.getLogger(StringUtils.class);
	
	
	public static int getPositionFromFamilies(String value,String[][] family) {
		for (int i = 0; i < family.length; i++)
			if ( value.equals(family[i][1]) )
				return i;
		log.warn("That should not append : " + value);
		log.warn("   Not found in: " + family);
		return 0;
	}
	
	public static String[] removeElementInArray(String[] input, String deleteMe) {
	    List<String> result = new LinkedList<String>();

	    for(String item : input)
	        if(!deleteMe.equals(item))
	            result.add(item);

	    return (String[]) result.toArray(input);
	}

}
