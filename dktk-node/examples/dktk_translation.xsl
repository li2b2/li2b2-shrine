<?xml version='1.0'?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ns7="http://schema.samply.de/ccp/Query" 
	xmlns:ns10="http://schema.samply.de/ccp/Case" 
	xmlns:ns11="http://schema.samply.de/ccp/Sample" 
	xmlns:ns12="http://schema.samply.de/ccp/QueryResultStatistic" 
	xmlns:ns13="http://schema.samply.de/ccp/Inquiry" 
	xmlns:ns14="http://schema.samply.de/ccp/Error" 
	xmlns:ns15="http://schema.samply.de/ccp/QueryResult" 
	xmlns:ns16="http://schema.samply.de/ccp/RorMetareg" 
	xmlns:ns17="http://schema.samply.de/ccp/Patient" 
	xmlns:ns2="http://schema.samply.de/ccp/MdrKey" 
	xmlns:ns3="http://schema.samply.de/ccp/Value" 
	xmlns:ns4="http://schema.samply.de/ccp/Attribute" 
	xmlns:ns5="http://schema.samply.de/ccp/MultivalueAttribute" 
	xmlns:ns6="http://schema.samply.de/ccp/RangeAttribute" 
	xmlns:ns8="http://schema.samply.de/ccp/Container" 
	xmlns:ns9="http://schema.samply.de/ccp/Entity">
<xsl:output indent="no" method="xml" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:strip-space elements="*" />

<xsl:param name="map" select="document('dktk_map.xml')"/>

<xsl:template name="mapping">
	<!-- 	Variablen:
			map:		tree from mapping-table
			item: 		subtree from i2b2-<panel>
			c_use: 		<use_i2b2_constraint/> is written in map/entry/dktk
						value range: true/false
			c_operator: operator of <constrain_by_value>
						value range: EQ/NE/GT/GE/LT/LE/IN/BETWEEN/LIKE[exact]/LIKE[begin]/LIKE[end]/LIKE[contains]/Contains[database]
			c_value: 	value of <constrain_by_value> 
			d_use:		<use_dktk_query> is written in map/entry/dktk
			mapEntry:	matching <entry>-node for item-->
	<xsl:param name="item"/>
	
	<xsl:if test="count($map//entry[i2b2/key = $item/item_key]) = 0">
		<xsl:message terminate="yes">
			Der Schluessel <xsl:value-of select="$item/item_key"/> der Quelldatei konnte keinem Eintrag der Map zugeordnet werden.
		</xsl:message>
	</xsl:if>
	<xsl:if test="count($map//entry[i2b2/key = $item/item_key]) > 1">
		<xsl:message terminate="yes">
			Der Schluessel <xsl:value-of select="$item/item_key"/> der Quelldatei konnte keinem Eintrag der Map eindeutig zugeordnet werden.
		</xsl:message>
	</xsl:if>
	
	<xsl:variable name="mapEntry" select="$map//entry[i2b2/key = $item/item_key]"/>
	<xsl:variable name="d_use" select="$mapEntry/dktk/use_dktk_query"/>
	<xsl:variable name="c_use" select="$mapEntry/dktk/use_i2b2_constraint"/>
	<xsl:variable name="c_operator" select="$item/constrain_by_value/value_operator"/>
	<xsl:variable name="c_value" select="$item/constrain_by_value/value_constraint"/>

	<!-- use constrain by date -->
	<xsl:if test="$c_use and $item/constrain_by_date">
		<xsl:message terminate="yes">
			Schluessel <xsl:value-of select="$item/item_key"/>:
			constrain_by_date in Quelldatei vorhanden, kann aber nicht verarbeitet werden.
		</xsl:message>
	</xsl:if>
	
	<!-- use constrain by value -->		
	<xsl:if test="not($c_use) and $item/constrain_by_value">
		<xsl:message terminate="yes">
			Schluessel <xsl:value-of select="$item/item_key"/>:
			constrain_by_value in Quelldatei vorhanden, aber fehlender Eintrag use_i2b2_constraint in Map.
		</xsl:message>
	</xsl:if>
	<xsl:if test="$c_use and $item/constrain_by_value and not($c_operator = 'EQ' or $c_operator = 'NE' 
					or $c_operator = 'GT' or $c_operator = 'GE' or $c_operator = 'LT' 
					or $c_operator = 'LE' or $c_operator = 'BETWEEN' or $c_operator = 'LIKE[exact]'
					or $c_operator = 'LIKE[begin]' or $c_operator = 'LIKE[end]' 
					or $c_operator = 'LIKE[contains]' or $c_operator = 'CONTAINS' 
					or $c_operator = 'CONTAINS[database]')">
		<xsl:message terminate="yes">
			Schluessel <xsl:value-of select="$item/item_key"/>:
			Nicht unterstuetzter Operator <xsl:value-of select="$c_operator"/> in Quelldatei vorhanden.
		</xsl:message>
	</xsl:if>
			
	<xsl:if test="$c_use and not($item/constrain_by_value)">
		<xsl:element name="ns7:IsNotNull">
			<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
		</xsl:element>
	</xsl:if>
		
	<xsl:if test="$c_operator = 'EQ'">
		<xsl:element name="ns7:Eq">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'NE'">
		<xsl:element name="ns7:Neq">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'GT'">
		<xsl:element name="ns7:Gt">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'GE'">
		<xsl:element name="ns7:Geq">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'LT'">
		<xsl:element name="ns7:Lt">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'LE'">
		<xsl:element name="ns7:Leq">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'BETWEEN'">
		<ns7:Between>
			<ns6:RangeAttribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:LowerBound><xsl:value-of select="substring-before($c_value,' and')"/></ns3:LowerBound>
				<ns3:UpperBound><xsl:value-of select="substring-after($c_value,'and ')"/></ns3:UpperBound>
			</ns6:RangeAttribute>
		</ns7:Between>
	</xsl:if>
	<xsl:if test="$c_operator = 'LIKE[exact]'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'LIKE[begin]'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value><xsl:value-of select="$c_value"/>%</ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'LIKE[end]'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value>%<xsl:value-of select="$c_value"/></ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'LIKE[contains]'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value>%<xsl:value-of select="$c_value"/>%</ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'CONTAINS'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value>%<xsl:value-of select="$c_value"/>%</ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<xsl:if test="$c_operator = 'CONTAINS[database]'">
		<xsl:element name="ns7:Like">
			<ns4:Attribute>
				<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				<ns3:Value>%<xsl:value-of select="$c_value"/>%</ns3:Value>
			</ns4:Attribute>
		</xsl:element>
	</xsl:if>
	<!-- TODO -->
	<xsl:if test="$c_operator = 'IN'"/>
	
	<!-- use dktk query -->
	<xsl:if test="$d_use">
		<xsl:copy-of select="$mapEntry/dktk/use_dktk_query/*"/>
	</xsl:if>
	
	<!-- use operator and value from mapping table -->
	<xsl:if test="$mapEntry/dktk/operator">
		<xsl:element name="{$mapEntry/dktk/operator}">
			<xsl:choose>
				<xsl:when test="$mapEntry/dktk/operator = 'ns7:IsNull' 
								or $mapEntry/dktk/operator = 'ns7:IsNotNull'">
					<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
				</xsl:when>
				<xsl:otherwise>
					<ns4:Attribute>
						<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
						<ns3:Value><xsl:value-of select="$mapEntry/dktk/value"/></ns3:Value>
					</ns4:Attribute>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:if>
</xsl:template>

<xsl:template match="/">
	<ns7:Query>
		<ns7:Where>
			<ns7:And>
				<xsl:for-each select="query_definition/panel">
					<ns7:Or>
						<xsl:for-each select="item">
							<xsl:call-template name="mapping">
								<xsl:with-param name="item" select="current()"/>
							</xsl:call-template>
						</xsl:for-each>
					</ns7:Or>
				</xsl:for-each>		
			</ns7:And>
		</ns7:Where>
	</ns7:Query>
</xsl:template>
</xsl:stylesheet> 