# Matting
The Application for Matting(A GAN trans photo to identification photo)
> 为证件照自动合成的GAN模型提供前端展示功能

### Version 0：
可支持三个功能
1. 调用本机相机，摄像并存至本地
2. 调用本地相册，选择图片（此功能设计时希望触发在Matting文件夹中添加新文件功能，然而此版本未实现）
3. 查看Matting文件夹，选择一张图片，通过Socket传递到服务器，服务器处理后，返回图片
        （此功能希望只显示Matting文件夹，然而此版本未实现）
        
### Version 1：
- 优化调用本机相册

### Version 2：
- 调用本机相机时，在取景框中添加矩形及人脸型框
- 调用本机相机时，最终仅保留取景框中结果
- 取消了Version 0 中功能2，改为查看Matting图库

### Version 3：
- 修复了4.4以上版本真机测试失败bug
- 优化取景框
- 优化真机测试保留取景框结果功能

### Version 4：
- 修复图像拍摄压缩成椭圆问题
- 添加了修改服务器及端口号功能
- 添加了背景色号的交互
- 优化了修改框的提示功能