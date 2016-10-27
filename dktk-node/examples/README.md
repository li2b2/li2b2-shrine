<table>
 <tr>
  <td>
  <br>
  Translate
  </td>
  <td>
  ______________________________________________<br/>&lt;query_definition&gt;<br/><span>                </span>&lt;query_name/&gt;<br/><span>                </span>&lt;query_timing/&gt;<br/><span>                </span>&lt;specificity_scale/&gt;<br/><span>                </span>&lt;panel&gt;<br/><span>                               </span>&lt;panel_number/&gt;<br/><span>                               </span>&lt;panel_accuracy_scale/&gt;<br/><span>                               </span>&lt;invert/&gt;<br/><span>                               </span>&lt;panel_timing/&gt;<br/><span>                </span><span>                </span>&lt;total_item_occurrences/&gt;<br/><span>                               </span>&lt;item/&gt;<br/><span>                               </span>&lt;item/&gt;<br/><span>                               </span>…<br/><span>                               </span>&lt;item/&gt;<br/><span>                </span>&lt;/panel&gt;<br/><span>                </span>&lt;panel/&gt;<br/><span>                </span>...<br/><span>                </span>&lt;panel/&gt;<br/>&lt;/query_definition&gt;<br/>
  </td>
  <td>
  by copying the query and only replacing &lt;item&gt;s according to
  the following mapping table description.
  </td>
 </tr>
</table>



<table>
 <tr>
  <td>
  ______________________________________________ mapping type
  </td>
  <td>
  ______________________________________________ i2b2 source
  </td>
  <td>
  ______________________________________________ mapentry
  </td>
  <td>
  ______________________________________________ i2b2 result
  </td>
 </tr>
 <tr>
  <td>
  Simple Key Mapping<br/><br/>Bsp.: Alter0-9 <span><span>--></span></span>
  Alter0-9
  </td>
  <td rowspan="4">
  &lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>SOURCEKEY</span>&lt;/item_key&gt;<span></span><br/>&lt;/item&gt;
  </td>
  <td>
  &lt;i2b2&gt;<br/><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;<br/>&lt;/i2b2&gt;<br/>&lt;local&gt;<br/><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;<br/>&lt;/local&gt;
  </td>
  <td>
  &lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;<br/>&lt;/item&gt;
  </td>
 </tr>
 <tr>
  <td>
  Multi Key Mapping<br/><br/>example: Alter0-9 <span><span>--></span></span>
  Alter0|Alter1|…|Alter9
  </td>
  <td>
  &lt;i2b2&gt;<br/><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;<br/>&lt;/i2b2&gt;<br/>&lt;local&gt;<br/><span>    </span>&lt;key&gt;<span>RESULTKEY</span>
  <span>1</span>&lt;/key&gt;<br/>&lt;/local&gt;<br/>…<br/>&lt;local&gt;<br/><span>    </span>&lt;key&gt;<span>RESULTKEY n</span>&lt;/key&gt;<br/>&lt;/local&gt;
  </td>
  <td>
  &lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>RESULTKEY 1</span>&lt;/item_key&gt;<br/>&lt;/item&gt;<br/>…<br/>&lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>RESULTKEY n</span>&lt;/item_key&gt;<br/>&lt;/item&gt;
  </td>
 </tr>
 <tr>
  <td>
  Constrain Mapping
  </td>
  <td>
  &lt;i2b2&gt;<br/><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;<br/>&lt;/i2b2&gt;<br/>&lt;local&gt;<br/><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;<br/><span>    </span><span>&lt;constrain_by_value/&gt;</span><br/>&lt;/local&gt;
  </td>
  <td>
  &lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;<br/><span>    </span><span>&lt;constrain_by_value/&gt;</span><br/>&lt;/item&gt;
  </td>
 </tr>
 <tr>
  <td>
  Modifier Mapping
  </td>
  <td>
  &lt;i2b2&gt;<br/><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;<br/>&lt;/i2b2&gt;<br/>&lt;local&gt;<br/><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;<br/><span>    </span><span>&lt;constrain_by_modifier&gt;</span><br/><span><span>       
  </span>&lt;constrain_by_value/&gt;</span><br/><span>    </span><span>&lt;/constrain_by_modifier&gt;</span><br/>&lt;/local&gt;
  </td>
  <td>
  &lt;item&gt;<br/><span>    </span><span>&lt;itemproperties/&gt;</span><br/><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;<br/><span>    </span><span>&lt;constrain_by_modifier&gt;</span><br/><span><span>       
  </span>&lt;constrain_by_value/&gt;</span><br/><span>    </span><span>&lt;/constrain_by_modifier&gt;</span><br/>&lt;/item&gt;
  </td>
 </tr>
</table>

<span>&lt;itemproperties/&gt; </span>= &lt;hlevel/&gt;,
&lt;item_key/&gt;, &lt;item_name/&gt;, &lt;tooltip/&gt;, &lt;item_icon/&gt;, &lt;class/&gt;,
&lt;constrain_by_value/&gt;, &lt;constrain_by_modifier/&gt;, &lt;item_is_synonym/&gt;,
…

</div>