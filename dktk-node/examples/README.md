<table>
 <tr>
  <td>
  <p><br>
  Translate</p>
  </td>
  <td>
  <p></p>
  <p>&lt;query_definition&gt;</p>
  <p><span>                </span>&lt;query_name/&gt;</p>
  <p><span>                </span>&lt;query_timing/&gt;</p>
  <p><span>                </span>&lt;specificity_scale/&gt;</p>
  <p><span>                </span>&lt;panel&gt;</p>
  <p><span>                               </span>&lt;panel_number/&gt;</p>
  <p><span>                               </span>&lt;panel_accuracy_scale/&gt;</p>
  <p><span>                               </span>&lt;invert/&gt;</p>
  <p><span>                               </span>&lt;panel_timing/&gt;</p>
  <p><span>                </span><span>                </span>&lt;total_item_occurrences/&gt;</p>
  <p><span>                               </span>&lt;item/&gt;</p>
  <p><span>                               </span>&lt;item/&gt;</p>
  <p><span>                               </span>…</p>
  <p><span>                               </span>&lt;item/&gt;</p>
  <p><span>                </span>&lt;/panel&gt;</p>
  <p><span>                </span>&lt;panel/&gt;</p>
  <p><span>                </span>...</p>
  <p><span>                </span>&lt;panel/&gt;</p>
  <p>&lt;/query_definition&gt;</p>
  <p></p>
  </td>
  <td>
  <p>by copying the query and only replacing &lt;item&gt;s according to
  the following mapping table description.</p>
  </td>
 </tr>
</table>

<p></p>

<table>
 <tr>
  <td>
  <p>mapping type</p>
  </td>
  <td>
  <p>i2b2 source</p>
  </td>
  <td>
  <p>mapentry</p>
  </td>
  <td>
  <p>i2b2 result</p>
  </td>
 </tr>
 <tr>
  <td>
  <p>Simple Key Mapping</p>
  <p></p>
  <p>Bsp.: Alter0-9 <span><span>--></span></span>
  Alter0-9</p>
  </td>
  <td>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>SOURCEKEY</span>&lt;/item_key&gt;<span></span></p>
  <p>&lt;/item&gt;</p>
  </td>
  <td>
  <p>&lt;i2b2&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;</p>
  <p>&lt;/i2b2&gt;</p>
  <p>&lt;local&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;</p>
  <p>&lt;/local&gt;</p>
  </td>
  <td>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;</p>
  <p>&lt;/item&gt;</p>
  </td>
 </tr>
 <tr>
  <td>
  <p>Multi Key Mapping</p>
  <p></p>
  <p>example: Alter0-9 <span><span>--></span></span>
  Alter0|Alter1|…|Alter9</p>
  </td>
  <td>
  <p>&lt;i2b2&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;</p>
  <p>&lt;/i2b2&gt;</p>
  <p>&lt;local&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>RESULTKEY</span>
  <span>1</span>&lt;/key&gt;</p>
  <p>&lt;/local&gt;</p>
  <p>…</p>
  <p>&lt;local&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>RESULTKEY n</span>&lt;/key&gt;</p>
  <p>&lt;/local&gt;</p>
  </td>
  <td>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>RESULTKEY 1</span>&lt;/item_key&gt;</p>
  <p>&lt;/item&gt;</p>
  <p>…</p>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>RESULTKEY n</span>&lt;/item_key&gt;</p>
  <p>&lt;/item&gt;</p>
  </td>
 </tr>
 <tr>
  <td>
  <p>Constrain Mapping</p>
  </td>
  <td>
  <p>&lt;i2b2&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;</p>
  <p>&lt;/i2b2&gt;</p>
  <p>&lt;local&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;</p>
  <p><span>    </span><span>&lt;constrain_by_value/&gt;</span></p>
  <p>&lt;/local&gt;</p>
  </td>
  <td>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;</p>
  <p><span>    </span><span>&lt;constrain_by_value/&gt;</span></p>
  <p>&lt;/item&gt;</p>
  </td>
 </tr>
 <tr>
  <td>
  <p>Modifier Mapping</p>
  </td>
  <td>
  <p>&lt;i2b2&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>SOURCEKEY</span>&lt;/key&gt;</p>
  <p>&lt;/i2b2&gt;</p>
  <p>&lt;local&gt;</p>
  <p><span>    </span>&lt;key&gt;<span>RESULTKEY</span>&lt;/key&gt;</p>
  <p><span>    </span><span>&lt;constrain_by_modifier&gt;</span></p>
  <p><span><span>       
  </span>&lt;constrain_by_value/&gt;</span></p>
  <p><span>    </span><span>&lt;/constrain_by_modifier&gt;</span></p>
  <p>&lt;/local&gt;</p>
  </td>
  <td>
  <p>&lt;item&gt;</p>
  <p><span>    </span><span>&lt;itemproperties/&gt;</span></p>
  <p><span>    </span>&lt;item_key&gt;<span>RESULTKEY</span>&lt;/item_key&gt;</p>
  <p><span>    </span><span>&lt;constrain_by_modifier&gt;</span></p>
  <p><span><span>       
  </span>&lt;constrain_by_value/&gt;</span></p>
  <p><span>    </span><span>&lt;/constrain_by_modifier&gt;</span></p>
  <p>&lt;/item&gt;</p>
  </td>
 </tr>
</table>

<p><span>&lt;itemproperties/&gt; </span>= &lt;hlevel/&gt;,
&lt;item_key/&gt;, &lt;item_name/&gt;, &lt;tooltip/&gt;, &lt;item_icon/&gt;, &lt;class/&gt;,
&lt;constrain_by_value/&gt;, &lt;constrain_by_modifier/&gt;, &lt;item_is_synonym/&gt;,
…</p>

</div>