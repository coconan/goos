# goos auction-sniper 分步骤源码

## 起步

1. 安装 openfire
2. 在 Intellj 中创建一个 Java Project
3. 添加 build.xml，内容参考最终源码
4. 创建 lib 目录，复制最终源码 lib 目录中的文件到 lib 目录
5. 创建 test/end-to-end 目录，右键点击 end-to-end 目录，选择 Mark Directory as ->  Test Root
6. 按书中章节，添加源码文件，直到能通过第一个测试用例

## 待办清单

1. [X] single item-join, lose without bidding 
2. [ ] single item-join, bid & lose
3. [ ] single item-join, bid & win
4. [ ] single item-show price details
5. [ ] multiple items
6. [ ] add new items through the GUI
7. [ ] Stop bidding at stop price 