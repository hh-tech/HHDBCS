图片格式只能是png
context是etc/icon目录下的子目录名称
context和name均为小写，非公用图片使用CsMgrEnum的类型作为context名称
图片名称规则
name.png //IconSizeEnum.OTHER
name_16.png //IconSizeEnum.SIZE_16的方形图片
name_16_disabled.png //IconSizeEnum.SIZE_16的方形禁用图片
name_disabled.png //IconSizeEnum.OTHER的禁用图片
 16*16， 32*32，64*64, 128*128大小的图片必须在name中标注

 例子可参照 HHDBCSv6.0中的about插件