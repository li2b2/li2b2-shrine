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
	xmlns:ns9="http://schema.samply.de/ccp/Entity"
	exclude-result-prefixes="ns2 ns3 ns4 ns5 ns6 ns7 ns8 ns9 ns10 ns11 ns12 ns13 ns14 ns15 ns16 ns17">
<xsl:output indent="yes" method="xml" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:strip-space elements="*" />

<!-- <xsl:param name="map" select="document('dktk_map.xml')"/> -->
<xsl:param name="mapURL" required="yes"/>
<xsl:param name="map" select="document($mapURL)"/>

<!-- im Falle von ns7:Between wird immer von <use_i2b2_constraint/> ausgegangen -->
<xsl:template match="*">
	<item>
		
		<xsl:variable name="dktk_key">
			<xsl:choose>
				<xsl:when test="name() = 'ns7:Between'">
					<xsl:value-of select="ns6:RangeAttribute/ns2:MdrKey"/>
				</xsl:when>
				<xsl:when test="(name() = 'ns7:IsNotNull') or (name() = 'ns7:IsNull')">
					<xsl:value-of select="ns2:MdrKey"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="ns4:Attribute/ns2:MdrKey"/>		
				</xsl:otherwise>		
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="dktk_operator" select="name()"/>
		<xsl:variable name="dktk_value" select="ns4:Attribute/ns3:Value"/>
		<xsl:variable name="dktk_valuelower" select="ns6:RangeAttribute/ns3:LowerBound"/>
		<xsl:variable name="dktk_valueupper" select="ns6:RangeAttribute/ns3:UpperBound"/>
		<xsl:variable name="i2b2_operator">
			<xsl:choose>
				<xsl:when test="$dktk_operator = 'ns7:Eq'">EQ</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Neq'">NE</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Gt'">GT</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Geq'">GE</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Lt'">LT</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Leq'">LE</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Like'">
					<xsl:if test="starts-with($dktk_value,'%') 
						and substring($dktk_value,string-length($dktk_value)) = '%'">LIKE[contains]</xsl:if>
					<xsl:if test="starts-with($dktk_value,'%')
						and not(substring($dktk_value,string-length($dktk_value)) = '%')">LIKE[begin]</xsl:if>
					<xsl:if test="not(starts-with($dktk_value,'%'))
						and substring($dktk_value,string-length($dktk_value)) = '%'">LIKE[end]</xsl:if>
					<xsl:if test="not(starts-with($dktk_value,'%'))
						and not(substring($dktk_value,string-length($dktk_value)) = '%')">LIKE[exact]</xsl:if>
				</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Between'">BETWEEN</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:IsNull'"/>
				<xsl:when test="$dktk_operator = 'ns7:IsNotNull'"/>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						Zusammengesetzter Schluessel aus
						Key: <xsl:value-of select="$dktk_key"/> 
						Operator: <xsl:value-of select="$dktk_operator"/>
						Value: <xsl:value-of select="$dktk_value"/>
						
						Nicht unterstuetzter Operator.
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="i2b2_value">
			<xsl:choose>
				<xsl:when test="$dktk_operator = 'ns7:Between'">
					<xsl:value-of select="$dktk_valuelower"/> and <xsl:value-of select="$dktk_valueupper"/>
				</xsl:when>
				<xsl:when test="$dktk_operator = 'ns7:Like'">
					<xsl:if test="starts-with($dktk_value,'%') 
						and substring($dktk_value,string-length($dktk_value)) = '%'">
						<xsl:value-of select="substring($dktk_value,2,string-length($dktk_value) - 2)"/>
					</xsl:if>
					<xsl:if test="starts-with($dktk_value,'%')
						and not(substring($dktk_value,string-length($dktk_value)) = '%')">
						<xsl:value-of select="substring($dktk_value,2)"/>
					</xsl:if>
					<xsl:if test="not(starts-with($dktk_value,'%'))
						and substring($dktk_value,string-length($dktk_value)) = '%'">
						<xsl:value-of select="substring($dktk_value,1,string-length($dktk_value) - 1)"/>
					</xsl:if>
					<xsl:if test="not(starts-with($dktk_value,'%'))
						and not(substring($dktk_value,string-length($dktk_value)) = '%')">
						<xsl:value-of select="$dktk_value"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$dktk_value"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- map entries with proper dktk key and <use_i2b2_constraint/> -->
		<xsl:variable name="me_i2b2c" select="$map//entry[dktk/key = $dktk_key and dktk/use_i2b2_constraint]"/>
		<!-- map entries with proper dktk key+operator+value 
			for IsNull and IsNotNull there is no value -->
		<xsl:variable name="me_kov" select="$map//entry[dktk/key = $dktk_key and dktk/operator = $dktk_operator
											and ((dktk/value = $dktk_value) or not($dktk_value))]"/>
		
		<xsl:variable name="i2b2_key"><xsl:choose>
				<xsl:when test="(count($me_i2b2c) = 1)
						and count($me_kov) = 0">
					<xsl:value-of select="$me_i2b2c/i2b2/key"/>					
				</xsl:when>				
				<xsl:when test="count($me_kov) = 1">
					<xsl:value-of select="$me_kov/i2b2/key"/>
				</xsl:when>				
				<xsl:otherwise>
					<xsl:message terminate="yes">
						Zusammengesetzter Schluessel aus
						Key: <xsl:value-of select="$dktk_key"/> 
						Operator: <xsl:value-of select="$dktk_operator"/>
						Value: <xsl:value-of select="$dktk_value"/>
						
						Es wurde kein (eindeutiger) entsprechender Eintrag in der Mapping-Tabelle gefunden.
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>		
		
		<xsl:choose>
			<xsl:when test="(count($me_i2b2c) = 1)
					and count($me_kov) = 0">
				<item_key><xsl:value-of select="$i2b2_key"/></item_key>
				<!-- Fallunterscheiden IsNotNull, dann sind operator und value nicht gesetzt -->
				<xsl:variable name="i2b2_type" select="$me_i2b2c/dktk/use_i2b2_constraint/@type"/>
				<xsl:variable name="i2b2_unit" select="$me_i2b2c/dktk/use_i2b2_constraint/@unit"/>
				<xsl:if test="not($i2b2_type) or not($i2b2_unit)">
					<xsl:message terminate="yes">
						Zusammengesetzter Schluessel aus
						Key: <xsl:value-of select="$dktk_key"/> 
						Operator: <xsl:value-of select="$dktk_operator"/>
						Value: <xsl:value-of select="$dktk_value"/>
						
						type und/oder unit Attribut fehlen in <use_i2b2_constraint/>.
					</xsl:message>
				</xsl:if>
				<xsl:if test="not($i2b2_operator = '') and not($i2b2_value = '')">
					<constrain_by_value>
						<value_type><xsl:value-of select="$i2b2_type"/></value_type>
						<value_unit_of_measure><xsl:value-of select="$i2b2_unit"/></value_unit_of_measure>
						<value_operator><xsl:value-of select="$i2b2_operator"/></value_operator>
						<value_constraint><xsl:value-of select="$i2b2_value"/></value_constraint>
					</constrain_by_value>
				</xsl:if>
			</xsl:when>
			
			<xsl:when test="count($me_kov) = 1">
				<item_key><xsl:value-of select="$i2b2_key"/></item_key>
			</xsl:when>
		</xsl:choose>	


		<hlevel>3</hlevel>		
		<item_name><xsl:value-of select="$i2b2_key"/></item_name>		
		<tooltip>i2b2 key: <xsl:value-of select="$i2b2_key"/> dktk key: <xsl:value-of select="$dktk_key"/></tooltip>
		<!-- ? --><class>ENC</class>
		<item_icon>LA</item_icon>
		<!-- ? --><item_is_synonym>false</item_is_synonym>
	</item>
</xsl:template>

<xsl:template match="/">
	<query_definition>
		<query_name>translated dktk query</query_name>
		<query_timing>ANY</query_timing>
		<specificity_scale>0</specificity_scale>
		<xsl:for-each select="ns7:View/ns7:Query/ns7:Where/ns7:And/ns7:Or">
			<panel>
				<panel_number><xsl:value-of select="position()"/></panel_number>			
				<!-- ? --><panel_accuracy_scale>100</panel_accuracy_scale>
				<!-- ? --><invert>0</invert>
				<!-- ? --><panel_timing>ANY</panel_timing>
				<!-- ? --><total_item_occurrences>1</total_item_occurrences>
				<xsl:apply-templates/>
			</panel>
		</xsl:for-each>	
	</query_definition>
</xsl:template>
</xsl:stylesheet> 