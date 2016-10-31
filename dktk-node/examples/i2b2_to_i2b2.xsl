<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output indent="yes" method="xml" encoding="utf-8" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*" />

	<xsl:param name="mapURL" required="yes"/>
	<xsl:param name="map" select="document($mapURL)"/>

	<xsl:template match="*">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="item">
		<!-- i2b2 source key -->
		<xsl:variable name="i2b2_key" select="item_key/text()"/>
		<!-- which has to be matched as entry/i2b2/key exactly one time -->
		<xsl:if test="not(count($map//entry[i2b2/key = $i2b2_key]) = 1)">
			<xsl:message terminate="yes">
				No match for key <xsl:value-of select="$i2b2_key"/> was found.
			</xsl:message>
		</xsl:if>
		
		<!-- entry in map file with corresponding entry/local/key -->
		<xsl:variable name="mapEntry" select="$map//entry[i2b2/key = $i2b2_key]"/>
		
		<!-- whole subtree of the source <item> -->
		<xsl:variable name="sourceItem" select="current()"/>
		<!-- which will be used as template for every corresponding local key -->
		<xsl:for-each select="$mapEntry/local">
			<xsl:element name="item">
				<!-- all subnodes but the <item_key> are copied exactly -->
				<xsl:apply-templates select="$sourceItem/*">
					<xsl:with-param name="localItemKey" select="current()/key"/>
				</xsl:apply-templates>
				<!-- in case of entry/local/constrain_by_value in map file that subtree will be copied -->
				<xsl:copy-of select="current()/constrain_by_value"/>
				<!-- in case of entry/local/constrain_by_modifier in map file that subtree will be copied -->
				<xsl:copy-of select="current()/constrain_by_modifier"/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<!-- <item_key> are replaced by the corresponding entry/local/key -->
	<xsl:template match="item_key">
		<xsl:param name="localItemKey"/>
		<xsl:copy><xsl:value-of select="$localItemKey"/></xsl:copy>
	</xsl:template>
</xsl:stylesheet> 