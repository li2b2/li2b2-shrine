<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output indent="no" method="xml" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:strip-space elements="*" />

<xsl:param name="map" select="document('i2b2_map.xml')"/>

<xsl:template match="*">
	<xsl:copy>
		<xsl:apply-templates />
    </xsl:copy>
</xsl:template>
<xsl:template match="item">
	<xsl:variable name="sourceItem" select="current()"/>
	<xsl:variable name="i2b2_key" select="item_key/text()"/>
	<xsl:if test="not(count($map//entry[i2b2/key = $i2b2_key]) = 1)">
		<xsl:message terminate="yes">
			Der Schluessel <xsl:value-of select="$i2b2_key"/> der Quelldatei konnte keinem Eintrag der Map (eindeutig) zugeordnet werden.
		</xsl:message>
	</xsl:if>
	<xsl:variable name="mapEntry" select="$map//entry[i2b2/key = $i2b2_key]"/>

	<xsl:for-each select="$mapEntry/local">
		<xsl:element name="item">
			<xsl:apply-templates select="$sourceItem/*">
				<xsl:with-param name="modifyItemKey" select="current()/key"/>
			</xsl:apply-templates>
			<xsl:copy-of select="current()/constrain_by_value"/>
		</xsl:element>
	</xsl:for-each>
</xsl:template>

<xsl:template match="item_key">
	<xsl:param name="modifyItemKey"/>
	<xsl:copy><xsl:value-of select="$modifyItemKey"/></xsl:copy>
</xsl:template>
</xsl:stylesheet> 