## Description

i2b2_to_i2b2.xsl translates an i2b2 query according to the rules listed below. In the mapping xml file the user can specify what `<item>`s (identified by their subnode `<item_key>`) should be mapped. One `<item>` can be mapped to one or more local `<item>`s while every item's properties (subnodes) are copied exactly. Also `<item>`s can be extended by a `<constrain_by_value>` and/or `<constrain_by_modifier>` subnode.

In general an i2b2 query looks like

```
<query_definition>
	<query_name/>
	<query_timing/>
	<specificity_scale/>
	<panel>
		<panel_number/>
		<panel_accuracy_scale/>
		<invert/>
		<panel_timing/>
		<total_item_occurrences/>
		<item/>
		<item/>
		…
		<item/>
	</panel>
	<panel/>
	...
	<panel/>
</query_definition>
```

where every `<item>` consists of an `<item_key>` and other item properties like `<hlevel/>`, `<item_key/>`, `<item_name/>`, `<tooltip/>`, `<item_icon/>`, `<class/>`, `<constrain_by_value/>`, `<constrain_by_modifier/>`, `<item_is_synonym/>`, etc.

## Translation Rules

### Simple Key Mapping
###### mapentry
```
<entry>
    <i2b2>
        <key>SOURCEKEY</key>
    </i2b2>
    <local>
        <key>RESULTKEY</key>
    </local>
</entry>
```
###### replacement
```diff
<item>
    itemproperties/subnodes
-   <item_key>SOURCEKEY</item_key>
+   <item_key>RESULTKEY</item_key>
</item>
```

### Multi Key Mapping
###### mapentry
```
<entry>
    <i2b2>
        <key>SOURCEKEY</key>
    </i2b2>
    <local>
        <key>RESULTKEY 1</key>
    </local>
    <local>
        <key>RESULTKEY 2</key>
    </local>
    ...
    <local>
        <key>RESULTKEY n</key>
    </local>
</entry>
```
###### replacement
```diff
-<item>
-   itemproperties/subnodes
-   <item_key>SOURCEKEY</item_key>
-</item>
+<item>
+   itemproperties/subnodes
+   <item_key>RESULTKEY 1</item_key>
+</item>
+<item>
+   itemproperties/subnodes
+   <item_key>RESULTKEY 2</item_key>
+</item>
+...
+<item>
+   itemproperties/subnodes
+   <item_key>RESULTKEY n</item_key>
+</item>
```

### Constrain Mapping
###### mapentry
```
<entry>
    <i2b2>
        <key>SOURCEKEY</key>
    </i2b2>
    <local>
        <key>RESULTKEY</key>
        <constrain_by_value/>
    </local>
</entry>
```
###### replacement
```diff
<item>
    itemproperties/subnodes
-   <item_key>SOURCEKEY</item_key>
+   <item_key>RESULTKEY</item_key>
+   <constrain_by_value/>
</item>
```

### Modifier Mapping
###### mapentry
```
<entry>
    <i2b2>
        <key>SOURCEKEY</key>
    </i2b2>
    <local>
        <key>RESULTKEY</key>
        <constrain_by_modifier>
            <constrain_by_value/>
        </constrain_by_modifier>
    </local>
</entry>
```
###### replacement
```diff
<item>
    itemproperties/subnodes
-   <item_key>SOURCEKEY</item_key>
+   <item_key>RESULTKEY</item_key>
+   <constrain_by_modifier>
+       <constrain_by_value/>
+   </constrain_by_modifier>
</item>
```