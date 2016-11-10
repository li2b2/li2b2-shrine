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
<xsl:output indent="yes" method="xml" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:strip-space elements="*" />

<xsl:param name="mapURL" required="yes"/>
<xsl:param name="map" select="document($mapURL)"/>
<!-- <xsl:param name="map" select="document('dktk_map.xml')"/> -->

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
	<xsl:variable name="mapEntry" select="$map//entry[i2b2/key = $item/item_key]"/>	
	
	<xsl:if test="not(count($mapEntry) = 1)">
		<xsl:message terminate="yes">
			Der Schluessel <xsl:value-of select="$item/item_key"/> der Quelldatei konnte keinem Eintrag der Map (eindeutig) zugeordnet werden.
		</xsl:message>
	</xsl:if>
	
	<xsl:variable name="d_use" select="$mapEntry/dktk/use_dktk_query"/>
	<xsl:variable name="c_use" select="$mapEntry/dktk/use_i2b2_constraint"/>

	<xsl:choose>		
		<!-- use operator and value from mapping table -->
		<xsl:when test="$mapEntry/dktk/key and not($c_use) and not($d_use)">
			<xsl:variable name="dktkop" select="$mapEntry/dktk/operator"/>
			<xsl:choose>
				<xsl:when test="$mapEntry/dktk/value and 
							($dktkop = 'ns7:Eq' or $dktkop = 'ns7:Neq' or $dktkop = 'ns7:Gt' or $dktkop = 'ns7:Geq' 
							or $dktkop = 'ns7:Lt' or $dktkop = 'ns7:Leq' or $dktkop = 'ns7:Like')">
					<xsl:element name="{$dktkop}">
						<ns4:Attribute>
							<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
							<ns3:Value><xsl:value-of select="$mapEntry/dktk/value"/></ns3:Value>
						</ns4:Attribute>
					</xsl:element>
				</xsl:when>					
				<xsl:when test="$dktkop = 'ns7:IsNull' or $dktkop = 'ns7:IsNotNull'">
					<xsl:element name="{$mapEntry/dktk/operator}">
						<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>					
					</xsl:element>
				</xsl:when>	
				<xsl:otherwise>
					<xsl:message terminate="yes">
						Der Schluessel <xsl:value-of select="$item/item_key"/> verweist auf einen fehlerhaften Map-Eintrag.
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>		
		<!-- use constrain by value -->
		<xsl:when test="$mapEntry/dktk/key and $c_use and (not($item/constrain_by_value) 
				or ($mapEntry/dktk/use_i2b2_constraint/@type = $item/constrain_by_value/value_type
				and $mapEntry/dktk/use_i2b2_constraint/@unit = $item/constrain_by_value/value_unit_of_measure))">
			<xsl:variable name="c_operator" select="$item/constrain_by_value/value_operator"/>
			<xsl:variable name="c_value" select="$item/constrain_by_value/value_constraint"/>
			<xsl:variable name="dktkop">
				<xsl:choose>
					<xsl:when test="$c_operator = 'EQ'">ns7:Eq</xsl:when>
					<xsl:when test="$c_operator = 'NE'">ns7:Neq</xsl:when>
					<xsl:when test="$c_operator = 'GT'">ns7:Gt</xsl:when>
					<xsl:when test="$c_operator = 'GE'">ns7:Geq</xsl:when>
					<xsl:when test="$c_operator = 'LT'">ns7:Lt</xsl:when>
					<xsl:when test="$c_operator = 'LE'">ns7:Leq</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							Schluessel <xsl:value-of select="$item/item_key"/>:
							Nicht unterstützter Operator <xsl:value-of select="$c_operator"/>.
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$c_operator = 'EQ' or $c_operator = 'NE' or $c_operator = 'GT' 
								or $c_operator = 'GE' or $c_operator = 'LT' or $c_operator = 'LE'">
					<xsl:element name="{$dktkop}">
						<ns4:Attribute>
							<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
							<ns3:Value><xsl:value-of select="$c_value"/></ns3:Value>
						</ns4:Attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="$c_operator = 'BETWEEN'">
					<ns7:Between>
						<ns6:RangeAttribute>
							<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
							<ns3:LowerBound><xsl:value-of select="substring-before($c_value,' and')"/></ns3:LowerBound>
							<ns3:UpperBound><xsl:value-of select="substring-after($c_value,'and ')"/></ns3:UpperBound>
						</ns6:RangeAttribute>
					</ns7:Between>
				</xsl:when>
				<xsl:when test="$c_operator = 'LIKE[exact]' or $c_operator = 'LIKE[begin]' or $c_operator = 'LIKE[end]' 
								or $c_operator = 'LIKE[contains]' or $c_operator = 'CONTAINS' or $c_operator = 'CONTAINS[database]'">
					<xsl:element name="ns7:Like">
						<ns4:Attribute>
							<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
							<ns3:Value>
								<xsl:if test="$c_operator = 'LIKE[begin]' or $c_operator = 'LIKE[contains]' 
												or $c_operator = 'CONTAINS' or $c_operator = 'CONTAINS[database]'">%</xsl:if>
								<xsl:value-of select="$c_value"/>
								<xsl:if test="$c_operator = 'LIKE[end]' or $c_operator = 'LIKE[contains]' 
												or $c_operator = 'CONTAINS' or $c_operator = 'CONTAINS[database]'">%</xsl:if>
							</ns3:Value>
						</ns4:Attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="$item/constrain_by_value and not($c_operator = 'EQ' or $c_operator = 'NE' 
								or $c_operator = 'GT' or $c_operator = 'GE' or $c_operator = 'LT' 
								or $c_operator = 'LE' or $c_operator = 'BETWEEN' or $c_operator = 'LIKE[exact]'
								or $c_operator = 'LIKE[begin]' or $c_operator = 'LIKE[end]' 
								or $c_operator = 'LIKE[contains]' or $c_operator = 'CONTAINS' 
								or $c_operator = 'CONTAINS[database]')">
					<xsl:message terminate="yes">
						Schluessel <xsl:value-of select="$item/item_key"/>:
						Nicht unterstuetzter Operator <xsl:value-of select="$c_operator"/> in Quelldatei vorhanden.
					</xsl:message>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="ns7:IsNotNull">
						<ns2:MdrKey><xsl:value-of select="$mapEntry/dktk/key"/></ns2:MdrKey>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>	
		<!-- use dktk query from mapping table -->
		<xsl:when test="$d_use">
			<xsl:copy-of select="$mapEntry/dktk/use_dktk_query/*"/>
		</xsl:when>
		<!-- use constrain by date -->
		<xsl:when test="$item/constrain_by_date">
			<xsl:message terminate="yes">
				Schluessel <xsl:value-of select="$item/item_key"/>:
				constrain_by_date in Quelldatei vorhanden, kann aber nicht verarbeitet werden.
			</xsl:message>
		</xsl:when>	
		<xsl:when test="not($c_use) and $item/constrain_by_value">
			<xsl:message terminate="yes">
				Schluessel <xsl:value-of select="$item/item_key"/>:
				constrain_by_value in Quelldatei vorhanden, aber fehlender Eintrag use_i2b2_constraint in Map.
			</xsl:message>
		</xsl:when>
		<xsl:otherwise>
			<xsl:message terminate="yes">
				Der Schluessel <xsl:value-of select="$item/item_key"/> verweist auf einen fehlerhaften Map-Eintrag.
			</xsl:message>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="/">
	<ns7:View>
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
		<ns7:ViewFields>
			<ns2:MdrKey>urn:dktk:dataelement:1:3</ns2:MdrKey>
		</ns7:ViewFields>
	</ns7:View>
</xsl:template>
</xsl:stylesheet> 