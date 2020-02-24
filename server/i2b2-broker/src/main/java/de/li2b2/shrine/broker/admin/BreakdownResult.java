package de.li2b2.shrine.broker.admin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;

public class BreakdownResult implements QueryResult, ResultType {
	String resultName;
	Integer setSize;
	List<Entry<String,Integer>> data;
	private BreakdownResult() {
	}

	public static class Item implements Entry<String,Integer>{
		private String key;
		private Integer value;
		public Item(String key, Integer value) {
			this.key = key;
			this.value =value;
		}
		@Override
		public String getKey() {return key;}

		@Override
		public Integer getValue() {return value;}

		@Override
		public Integer setValue(Integer value) {this.value = value;return null;}
		
	}
	public static BreakdownResult parseElement(Element element) {
		BreakdownResult me = new BreakdownResult();
		me.resultName = element.getAttribute("name");
		me.data = new ArrayList<>();
		if( element.hasAttribute("set-size") ) {
			me.setSize = Integer.parseInt(element.getAttribute("set-size"));
		}
		NodeList nl = element.getElementsByTagName("data");
		for( int i=0; i<nl.getLength(); i++ ) {
			Element de = (Element)nl.item(i);
			Integer value;
			try {
				value = Integer.parseInt(de.getTextContent().trim());
			}catch( NumberFormatException e ) {
				value = null;
			}
			me.data.add(new Item(de.getAttribute("column"), value));
		}
		return me;
	}
	@Override
	public ResultType getResultType() {
		return this;
	}

	@Override
	public Integer getSetSize() {
		return setSize;
	}

	@Override
	public Instant getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instant getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryStatus getStatus() {
		return QueryStatus.COMPLETED;
	}

	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		return data;
	}
	@Override
	public String getName() {
		return resultName;
	}
	@Override
	public String getDisplayType() {
		return "CATNUM";
	}
	@Override
	public String getDescription() {
		return resultName;
	}

}
