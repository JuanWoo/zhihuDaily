package main.story;

import com.jfinal.plugin.activerecord.Model;


/**
tableName: img_url
+-----------+-------------------+-------+-------+-----------+-------+
| Field		| Type				| Null	| Key	| Default	| Extra	|
+-----------+-------------------+-------+-----+-------------+-------+
| id		| decimal(20,0)		| NO	| PRI	| NULL		|		|
| type		| decimal(2,0)		| YES	|		| 0 		|		|
| url		| varchar2(500)		| YES	|		| ''		|		|
| img_date	| date				| YES	|		| NULL		|		|
+-----------+-------------------+-------+-------+-----------+-------+
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

@SuppressWarnings("serial")
public class ImgUrlModel extends Model<ImgUrlModel>{
	public static final ImgUrlModel dao = new ImgUrlModel();
}
