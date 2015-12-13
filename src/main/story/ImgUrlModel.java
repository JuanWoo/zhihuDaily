package main.story;

import com.jfinal.plugin.activerecord.Model;


/**
tableName: img_url
+-----------+-------------------+-------+-------+-----------+-------+
| Field		| Type				| Null	| Key	| Default	| Extra	|
+-----------+-------------------+-------+-----+-------------+-------+
| id		| decimal(10,0)		| NO	| PRI	| NULL		|		|
| url		| varchar2(500)		| NO	|		| NULL		|		|
| img_date	| date				| NO	|		| NULL		|		|
+-----------+-------------------+-------+-------+-----------+-------+
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

@SuppressWarnings("serial")
public class ImgUrlModel extends Model<ImgUrlModel>{}
