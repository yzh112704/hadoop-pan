# 代码简述

### 后端JAVA代码结构

#### - config		初始化配置文件

##### 	-- initData		hbase数据库及hadoop分布式文件系统初始化

​		建表初始化，所需要的数据表不存在则新建

​		禁用文件替换数据，不存在时数据库表新建信息、上传要替换的文件到hadoop上

##### 	-- KapatchaConfig		验证码生成配置

```java
// 图片宽、高、字体大小、字体颜色、验证码文本内容范围、验证码文本长度、调用接口
properties.setProperty("kaptcha.image.width","100");
properties.setProperty("kaptcha.image.height","40");
properties.setProperty("kaptcha.textproducer.font.size","32");
properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
properties.setProperty("kaptcha.textproducer.char.string","023456789ABCDEFGHIJKMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
properties.setProperty("kaptcha.textproducer.char.length","4");
properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
```

##### 	-- WebMvcConfig		Spring Web MVC的处理器拦截器

​		类似于Servlet开发中的过滤器Filter，用于对处理器进行预处理和后处理

#### - controller		接口控制器

##### 	-- interceptor		拦截器

###### 		--- LoginTicketInterceptor.java		登录拦截器

```java
preHandle
    判断是否拥有Cookie登录凭证，用户（计算用户的显示空间大小）或管理员
postHandle
    设置loginUser或loginAdmin对象
afterCompletion
    清除对象
```

##### 	--AdminController.java		管理员接口控制器

```java
	/**
     * 跳转到管理员登录页面
     * @return
     */
    @RequestMapping(value = "/adminLogin")
    public String adminIndex(){return "admin/adminLogin";}    

	/**
     * 管理员登录
     * @param model
     * @param adminid           输入的管理员ID
     * @param adminphone        输入的管理员电话
     * @param adminpassword     输入的管理员密码
     * @param response          响应（Cookie）
     * @return
     */
    @RequestMapping("/adminLogin")
    public String adminLogin(Model model
            , String adminid
            , String adminphone
            , String adminpassword
            , HttpServletResponse response) {}

    /**
     * 管理员添加用户
     * @param model
     * @param user              用户对象信息
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminAddUser")
    public String adminAddUser(Model model
            ,User user
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

	/**
     * 批量添加用户
     * @param users
     * @return
     */
    @RequestMapping(path = "/addUsers", method = RequestMethod.POST)
    @ResponseBody
    public String adminAddUsers(@RequestParam("users") String users) {}

    /**
     * 管理员添加管理员
     * @param model
     * @param newAdmin          要添加的管理员对象信息
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminAddAdmin")
    public String adminAddAdmin(Model model
            ,Admin newAdmin
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

    /**
     * 显示所有用户列表或文件列表
     * @param model
     * @param type
     * @param adminTicket       管理员Cookie凭证
     * @return
     */
    @RequestMapping("/allList")
    public String allList(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

    /**
     * 返回用户列表数据
     * @return List<User>
     */
    @RequestMapping(path = "/allListUsers", method = RequestMethod.POST)
    @ResponseBody
    public List<User> allListUsers() {}

    /**
     * 返回文件列表数据
     * @return List<File>
     */
    @RequestMapping(path = "/allListFiles", method = RequestMethod.POST)
    @ResponseBody
    public List<File> allListFiles() {}

    /**
     * 返回被禁用文件列表数据
     * @return List<File>
     */
    @RequestMapping(path = "/allListBanFiles", method = RequestMethod.POST)
    @ResponseBody
    public List<File> allListBanFiles() {}

    /**
     * 返回分享链接列表
     * @return List<Share>
     */
    @RequestMapping(path = "/allListShares", method = RequestMethod.POST)
    @ResponseBody
    public List<Share> allListShares() {}

    /**
     * 用户信息修改
     * @param model
     * @param user          用户新信息
     * @param space         空间大小
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminEditUser")
    public String adminEditUser(Model model
                                , User user
                                , String space
                                , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket){}

    /**
     * 管理员信息修改
     * @param model
     * @param editAdmin     新管理员信息
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminEditAdmin")
    public String adminEditAdmin(Model model
                                 , Admin editAdmin
                                 , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket){}

    /**
     * 管理员下载文件
     * @param request   获取请求的路径
     * @param md5     要下载的文件md5
     * @return String url
     */
    @RequestMapping(path = "/AdminDownloadFile", method = RequestMethod.POST)
    @ResponseBody
    public String AdminDownloadFile(HttpServletRequest request
            , @RequestParam(value = "md5", defaultValue = "null") String md5){}

    /**
     * 显示分析数据
     * @param model
     * @param type          要分析的类型
     * @param top           排行
     * @param startDate     起始日期 yyyy-MM-dd
     * @param endDate       结束日期 yyyy-MM-dd
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/analysis")
    public String analysis(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "top", defaultValue = "3") String top
            , @RequestParam(value = "startDate", defaultValue = "2020-11-28") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

    /**
     * 按天分析数据（每日上传量、下载量、分享量）
     * @param startDate
     * @param endDate
     * @return	List<List<List<String>>>		[[['date','count'],[]],[]]
     */
    @RequestMapping(path = "/analysisByDay", method = RequestMethod.POST)
    @ResponseBody
    public List<List<List<String>>> analysisByDay(@RequestParam(value = "startDate", defaultValue = "2020-11-28") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate) {}

    /**
     * 用户文件排行榜（用户 上传、下载、分享数，文件 被上传、被下载数）
     * @param startDate     起始日期
     * @param endDate       结束日期
     * @param top           排行
     * @return	List<List<List<String>>>		[[['count', 'top', 'id'],[]],[]]
     */
    @RequestMapping(path = "/analysisTop", method = RequestMethod.POST)
    @ResponseBody
    public List<List<List<String>>> analysisTop(@RequestParam(value = "startDate", defaultValue = "2020-11-28") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @RequestParam(value = "top", defaultValue = "3") String top) {}

    /**
     * 分享被访问数
     * @param startDate     起始日期
     * @param endDate       结束日期
     * @param top           排行
     * @return	List<List<String>>		[[['count', 'id', 'md5'],[]],[]]
     */
    @RequestMapping(path = "/analysisShareTop", method = RequestMethod.POST)
    @ResponseBody
    public List<List<String>> analysisShareTop(@RequestParam(value = "startDate", defaultValue = "2020-11-28") String startDate
            , @RequestParam(value = "endDate", defaultValue = "null") String endDate
            , @RequestParam(value = "top", defaultValue = "3") String top) {}

    /**
     * 禁用文件
     * @param type      类型（文件、分享链接）
     * @param flag      文件是否保存
     * @param name      新文件名
     * @param id        文件ID或链接MD5
     * @return "1"
     */
    @RequestMapping("/ban")
    @ResponseBody
    public String ban(@RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "flag", defaultValue = "null") String flag
            , @RequestParam(value = "name", defaultValue = "null") String name
            , @RequestParam(value = "id", defaultValue = "null") String id ){}

    /**
     * 显示详细信息
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/showInfo")
    public String showInfo(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

    /**
     * 管理员操作（添加或修改信息）
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminChange")
    public String adminChange(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

    /**
     * 管理员删除
     * @param model
     * @param type          类型（用户或管理员）
     * @param id            ID
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminDel")
    public String adminDel(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "id", defaultValue = "null") String id
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}

	/**
     * 管理员批量删除用户
     * @param model
     * @param type          类型（用户或管理员）
     * @param ids            IDs
     * @param adminTicket   管理员Cookie凭证
     * @return
     */
    @RequestMapping("/adminDelUsers")
    public String adminDelUsers(Model model
            , @RequestParam(value = "type", defaultValue = "null") String type
            , @RequestParam(value = "ids", defaultValue = "null") String ids
            , @CookieValue(value = "adminTicket", defaultValue = "null") String adminTicket) {}
```

##### 	-- FileController.java		文件控制器

```java
    /**
     * 列出文件夹列表
     * @param model
     * @param dirId     要列出的文件夹ID（即当前显示列表的父ID）
     * @param type      排序类型（名字、大小、日期）
     * @param upOrDown  排序方式（升序、降序）
     * @return
     */
    @RequestMapping(path = "/dirList", method = RequestMethod.GET)
    public String dirList(Model model
            , @RequestParam(value = "dirId", defaultValue = "null") String dirId
            , @RequestParam(value = "type", defaultValue = "name") String type
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown){}

    /**
     * 返回上一级目录
     * @param model
     * @param parentId      当前目录的父ID
     * @param type          排序类型（名字、大小、日期）
     * @param upOrDown      排序方式（升序、降序）
     * @return
     */
    @RequestMapping(path = "/returnDirList", method = RequestMethod.GET)
    public String returnDirList(Model model
            , @RequestParam(value = "parentId", defaultValue = "null") String parentId
            , @RequestParam(value = "type", defaultValue = "name") String type
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown) {}

    /**
     * 列出文件
     * @param model
     * @param fileType  文件类型（图片、文档、视频）
     * @param type      排序类型（名字、大小、日期）
     * @param upOrDown  排序方式（升序、降序）
     * @return
     */
    @RequestMapping(path = "/fileList", method = RequestMethod.GET)
    public String fileList(Model model
            , @RequestParam(value = "fileType", defaultValue = "fileType") String fileType
            , @RequestParam(value = "type", defaultValue = "name") String type
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown) {}

    /**
     * 检查文件
     * 1、文件是否存在     不存在新建，存在执行2、3步
     * 2、文件大小是否超出限制（1GB）
     * 3、当前已经上传的块数
     * @param md5           文件MD5
     * @param fileName      文件名
     * @return code			已存在且上传完成（秒传）、已存在且未上传完成（当前块数）、不存在（"1"）
     */
    @RequestMapping(path = "/checkFile", method = RequestMethod.POST)
    @ResponseBody
    public String checkFile(@RequestParam("md5") String md5
            ,@RequestParam("fileName") String fileName){}

    /**
     * 上传文件
     * @param md5               上传文件md5
     * @param currentChunk      当前块编号
     * @param totalChunks       总块数
     * @param data              块内容
     * @param request           用户获取路径
     * @return	code			成功、文件过大（超过1GB）
     */
    @RequestMapping(path = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("md5") String md5
            , @RequestParam("currentChunk") String currentChunk
            , @RequestParam("totalChunks") String totalChunks
            , @RequestParam("data") MultipartFile data
            , HttpServletRequest request){}

    /**
     * 数据库上传文件信息
     * @param userId        用户ID
     * @param parentId      上传到父文件夹ID内
     * @param md5           文件md5
     * @return	code		成功、重复、空间不足
     */
    @RequestMapping(path = "/uploadFileInfo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileInfo(@RequestParam(value = "userId") String userId
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "md5") String md5){}

    /**
     * 新建文件夹
     * @param dirName       文件夹名
     * @param parentId      父文件夹ID
     * @param userId        用户ID
     * @return code			成功、重复
     */
    @RequestMapping(path = "/makeDir", method = RequestMethod.POST)
    @ResponseBody
    public String makeDir(@RequestParam(value = "dirName") String dirName
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "userId") String userId){}

    /**
     * 重命名文件夹或文件
     * @param dirId         要重命名的文件夹或文件ID
     * @param newName       新名字
     * @return	code		成功、重复
     */
    @RequestMapping(path = "/rename", method = RequestMethod.POST)
    @ResponseBody
    public String rename(@RequestParam(value = "dirId") String dirId
            , @RequestParam(value = "newName") String newName){}

    /**
     * 下载文件
     * @param request   获取请求的路径
     * @param dirId     要下载的文件ID（文件夹ID）
     * @return url
     */
    @RequestMapping(path = "/downloadFile", method = RequestMethod.POST)
    @ResponseBody
    public String downloadFile(HttpServletRequest request
            , @RequestParam(value = "dirId", defaultValue = "null") String dirId){}

    /**
     * 删除文件夹
     * @param dirIds    要删除的文件夹IDs
     * @param userId    用户ID
     * @return	"1"
     */
    @RequestMapping(path = "/deleteDirs", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId){}

    /**
     * 移动或复制或保存时显示的文件列表
     * @param model
     * @param dirIds        要移动或复制或保存的文件夹IDs
     * @param userId        用户ID
     * @param dirId         当前目录的文件夹ID
     * @param type          类型（copy复制、move移动、save保存）
     * @return
     */
    @RequestMapping(path = "/copyOrMoveOrSaveDirList", method = RequestMethod.GET)
    public String copyOrMoveOrSaveDirList(Model model
            , @RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId
            , @RequestParam(value = "dirId") String dirId
            , @RequestParam(value = "type") String type){}

    /**
     * 移动或复制或保存时显示的文件列表返回上一级
     * @param model
     * @param dirIds        要移动或复制或保存的文件夹IDs
     * @param userId        用户ID
     * @param dirId         当前目录的文件夹ID
     * @param type          类型（copy复制、move移动、save保存）
     * @return
     */
    @RequestMapping(path = "/returnCopyOrMoveOrSaveDirList", method = RequestMethod.GET)
    public String returnCopyOrMoveOrSaveDirList(Model model
            , @RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId
            , @RequestParam(value = "dirId") String dirId
            , @RequestParam(value = "type") String type){}

    /**
     * 移动或复制或保存文件夹
     * @param dirIds        要移动或复制或保存的文件夹IDs
     * @param userId        用户ID
     * @param parentId      父文件夹ID
     * @param type          类型（copy复制、move移动、save保存）
     * @return	code		复制、移动、保存	成功、失败（已经存在、其内、空间不足）
     */
    @RequestMapping(path = "/copyOrMoveOrSaveDirs", method = RequestMethod.POST)
    @ResponseBody
    public String copyOrMoveOrSaveDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "type") String type){}

    /**
     * 分享文件夹
     * @param dirIds        要分享的文件IDs
     * @param choice        选择的分享时效（1天、1周、1月、永久）
     * @return  url         生成的链接md5部分信息
     */
    @RequestMapping(path = "/shareDirs", method = RequestMethod.POST)
    @ResponseBody
    public String shareDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "choice") String choice){}

    /**
     * 分享链接的认证凭证
     * 1、其他用户需要输入口令后才生成认证凭证
     * 2、管理员、用户自己生成认证凭证，不需要输入口令
     * @param model
     * @param share_md5     分享链接地址的share_md5值
     * @param shareTicket   打开分享链接的凭证（12小时内是否已经输入过口令）
     * @param response      响应（用户自己或管理员响应认证凭证）
     * @return
     */
    @RequestMapping(path = "/share/{share_md5}", method = RequestMethod.GET)
    public String getShareDirs(Model model
            , @PathVariable(value = "share_md5") String share_md5
            , @CookieValue(value = "shareTicket", defaultValue = "null") String shareTicket
            , HttpServletResponse response){}

    /**
     * 分享认证检测（是否拥有Cookie凭证）
     * @param model
     * @param key           提交的口令
     * @param share_md5     链接share_md5
     * @param response      添加认证通过的cookie
     * @return
     */
    @RequestMapping("/submitKey")
    public String checkShareKey(Model model
            , String key
            , String share_md5
            , HttpServletResponse response){}

    /**
     * 浏览分享内容
     * @param model
     * @param share_md5         链接share_md5
     * @param dirIds            查看的文件夹IDs
     * @param parentId          父文件夹ID
     * @param shareTicket       认证凭证
     * @return
     */
    @RequestMapping(path = "/share/scan/{share_md5}", method = RequestMethod.GET)
    public String scanShareDirs(Model model
            , @PathVariable(value = "share_md5") String share_md5
            , @RequestParam(value = "dirIds", defaultValue = "") String dirIds
            , @RequestParam(value = "parentId", defaultValue = "") String parentId
            , @CookieValue(value = "shareTicket", defaultValue = "null") String shareTicket){}

    /**
     * 浏览分享内容返回上一级
     * @param model
     * @param share_md5         链接share_md5
     * @param dirIds            查看的文件夹IDs
     * @param parentId          父文件夹ID
     * @param shareTicket       认证凭证
     * @return
     */
    @RequestMapping(path = "/share/returnScan/{share_md5}", method = RequestMethod.GET)
    public String returnScanShareDirs(Model model
            , @PathVariable(value = "share_md5") String share_md5
            , @RequestParam(value = "dirIds", defaultValue = "") String dirIds
            , @RequestParam(value = "parentId", defaultValue = "") String parentId
            , @CookieValue(value = "shareTicket", defaultValue = "null") String shareTicket){}
```

##### 	-- UserController.java

```java
        /**
         * 主页面（用户登录页面）
         * @param model
         * @param ticket 是否已经登录过
         * @return
         */
    @RequestMapping(value = {"/index", "/"})
    public String index(Model model
            , @CookieValue(value = "ticket", defaultValue = "null") String ticket) {}

    /**
     * 注册账号
     * @param model
     * @param user          用户信息对象
     * @param confirmPwd    确认密码
     * @param request       获取地址（用于发送邮件的链接地址）
     * @return
     */
    @RequestMapping("/userRegister")
    public String register(Model model
            ,User user
            , String confirmPwd
            , HttpServletRequest request){}

    /**
     * 邮件内容注册链接
     * @param model
     * @param mail_md5      邮件MD5
     * @return
     */
    @RequestMapping("/action/{mail_md5}")
    public String register(Model model
            , @PathVariable(value = "mail_md5") String mail_md5) {}

    /**
     * 用户自己修改密码（登录后）
     * @param model
     * @param id            用户ID
     * @param old           旧密码
     * @param pwd           新密码
     * @param confirmPwd    确认新密码
     * @return
     */
    @RequestMapping("/userChangePwd")
    public String changePwd(Model model
            , String id
            ,@RequestParam(value="old", defaultValue="") String old
            , String pwd
            , String confirmPwd) {}

    /**
     * 用户修改密码跳转（是否已登录，有登录凭证）
     * @param model
     * @param ticket
     * @return
     */
    @RequestMapping("/user/changePwd")
    public String userChangePwd(Model model, @CookieValue(value = "ticket", defaultValue = "null") String ticket) {}


    /**
     * 生成验证码
     * @param response      响应验证码图片
     * @param session       设置验证码文本内容
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response
            , HttpSession session){}

    /**
     * 用户登录
     * @param model
     * @param user          用户登录信息对象
     * @param captcha       输入的验证码
     * @param rememberMe    是否记住用户（不记住：半天；记住：100天）
     * @param session       获取验证码信文本
     * @param response
     * @return
     */
    @RequestMapping("/userLogin")
    public String login(Model model
            , User user
            , String captcha
            , boolean rememberMe
            , HttpSession session
            , HttpServletResponse response) {}

    /**
     * 退出登录
     * @param model
     * @param ticket        用户认证凭证
     * @return
     */
    @RequestMapping(path = "/userLogout", method = RequestMethod.GET)
    public String logout(Model model
                         , @CookieValue("ticket") String ticket){}

    /**
     * 跳转到用户修改信息页面（赋值数据库内容）
     * @param model
     * @param ticket    用户登录凭证
     * @return
     */
    @RequestMapping("/user/editInfo")
    public String userEditInfo(Model model, @CookieValue("ticket") String ticket){}

    /**
     * 用户信息修改
     * @param model
     * @param user          用户信息对象
     * @param request       修改邮箱时发送解绑邮件
     * @return
     */
    @RequestMapping("/userEdit")
    public String edit(Model model
            , User user
            ,HttpServletRequest request){}

    /**
     * 跳转到忘记密码页面
     * @return
     */
    @RequestMapping("/user/forgot")
    public String toForgot() {}

    /**
     * 忘记密码
     * @param model
     * @param user      用户对象信息
     * @param request   忘记密码邮件
     * @return
     */
    @RequestMapping("/userForgot")
    public String forgot(Model model, User user, HttpServletRequest request) {}
```

#### - dao		数据库操作

##### 	-- basedao	基本操作

###### 		--- HbaseDao.java		Hbase基本操作

```java
    /**
     * 一次性插入或修改多条数据,针对列族中有多个列
     * @category put 'tableName','rowKey','familyName:columnName'
     * @param tableName     表名
     * @param rowKey        行键
     * @param family        列簇名
     * @param column        列名
     * @param value         列值
     * @throws IOException
     */
    public void updateMoreData(String tableName, String rowKey,  String family, String[] column, String[] value) {}

    /**
     * 获得一行数据，行健为字符串类型
     * @category get 'tableName','rowKey'
     * @param tableName         表名
     * @param rowKey            行键
     * @return Result||null
     * @throws IOException
     */
    public Result getResultByRow(String tableName, String rowKey) {}

    /**
     * 插入或修改一条数据，针对列族中有一个列，value为String类型
     * @category put 'tableName','rowKey','familyName:columnName'
     * @param tableName         表名
     * @param rowKey            行键
     * @param family            列簇名
     * @param column            列名
     * @param value             列值
     * @throws IOException
     */
    public void updateOneData(String tableName, String rowKey, String family, String column, String value) {}

    /**
     * 计算行数，得到唯一的id值
     * @param tableName         表名
     * @return long
     */
    public long getRowNum(String tableName){}

    /**
     * 得到表内所有rowkey
     * @param tableName     表名
     * @return List<String>
     */
    public List<String> getRowKey(String tableName){}

    /**
     * 删除某一行的数据，行健为String类型
     * @category deleteAll 'tableName','rowKey'
     * @param tableName         表名
     * @param rowKey            行键
     * @throws IOException
     */
    public void deleteDataByRow(String tableName, String rowKey) {}
    /**
     * 删除某一行中某一列的数据，行健为String类型，列名为String类型
     * @category delete 'tableName','rowKey','familyName:columnName'
     * @param tableName         表名
     * @param rowKey            行键
     * @param familyName        列簇名
     * @param columnName        列名
     * @throws IOException
     */
    public void deleteDataByColumn(String tableName, String rowKey, String familyName, String columnName) {}

    /**
     * 按照一定规则扫描表（根据开始、结束字段进行过滤，节省时间）
     * @param tableName             表名
     * @param filter                过滤器
     * @param startRow              起始行键
     * @param stopRow               结束行键
     * @return ResultScanner
     * @throws IOException
     */
    public ResultScanner getResultScannerByFilterAndRow(String tableName
            , Filter filter
            , String startRow
            , String stopRow) {}

    /**
     * 按照一定规则扫描表（根据开始、结束字段进行过滤，节省时间；根据时间过滤获得倒某个时间段内的数据）
     * @param tableName             表名
     * @param filter                过滤器
     * @param startRow              起始行键
     * @param stopRow               结束行键
     * @param startTime             起始时间
     * @param stopTime              结束时间
     * @return ResultScanner
     */
    public ResultScanner getResultScannerByFilterAndRowAndTime(String tableName, Filter filter
            , String startRow, String stopRow
            , Long startTime, Long stopTime) {}
```

###### 		--- HdfsDao.java		Hdfs基本操作

```java
    // hdfs文件存储路径
    private final String basePath = "/OnlineDisk/";

    /**
     * 上传文件
     * @param inputStream   输入流（文件IO流）
     * @param md5           文件md5
     * @return
     */
    public String put(InputStream inputStream, String md5) {}

    /**
     * 下载文件
     * @param downloadName          下载名字
     * @param fileMd5               文件md5
     * @param local                 下载到的路径（服务器运行的路径）
     */
    public boolean download(String downloadName, String fileMd5, String local) {}

    /**
     * hdfs重命名文件（存储文件重命名）
     * @param fileMd5           文件md5
     * @param newName           新文件名
     */
    public void renameFile(String fileMd5,String newName) {}

    /**
     * 复制或者移动文件（禁用文件）
     * @param banFileId     要替换成的文件
     * @param destFileId    需要被替换的文件
     */
    public void copyOrMove(String banFileId, String destFileId) {}
```

##### 	-- conn	连接

###### 		--- HbaseConn.java		Hbase连接

```java
    // 连接hbase数据库
    private HbaseConn() {}

    //获取连接
    public static final Connection getConn() {}

    // Hbase获取所有的表信息
    public List getAllTables() {}
```

###### 		--- HdfsConn.java		Hdfs连接

```java
	// 连接hdfs
    private HdfsConn() {}

	// 文件操作
    public static FileSystem getFileSystem() {}

	// 配置
    public static Configuration getConfiguration() {}
```

##### 	-- AdminDao.java		管理员表数据库操作

```java
	// 向Admin表添加管理员
    public void addAdmin(Admin admin){}

    // 根据管理员ID获取管理员信息
    public Admin getAdminInfoById(String id) {}

    // 删除管理员信息
    public void delAdminInfoById(String id){}
```

##### 	-- AnalysisDao.java		分析表数据库操作

```java
    // 向analysis表添加分析数据
    public void addAnalysis(Analysis analysis){}

    // 获取到分析信息
    public Analysis getAnalysisInfoByRowKey(String rowKey) {}

    // 获取分析数据的count值
    public String getAnalysisCount(String rowKey){}

    // 获取分析数据列表
    public List<Analysis> getMyAnalysisList(Filter filter, String startRow, String stopRow) {}

    // 根据日期获取分析数据
    public List<Analysis> getMyAnalysisListByDate(Filter filter, String startRow, String stopRow, Long startTime, Long stopTime) {}

```

##### 	-- DirDao.java		文件夹表数据库操作

```java
    // 向dir表中添加文件夹的信息
    public String addDirInfo(Dir dir) {}

    // 向dir表中删除文件夹信息与文件信息,保留rowkey，isDir值为空值""
    public void delDirInfo(String dirId) {}

    // 根据文件夹id找到文件夹名、父文件夹id、目录级别、路径
    public Dir getDirInfoById(String id) {}

    // dir表按行数计算文件夹ID

    public String getDirIdByTableCount() {}
```

##### 	-- FileDao.java		文件表数据库操作

```java
    // 上传文件到hdfs中
    public String upload(InputStream inputStream, String md5) {}

    // 下载文件，从hdfs中
    public boolean downloadFile(String downloadName, String fileMd5, String local) {}


    // 向file表中添加文件的信息
    public String addFileInfo(File file) {}


    // 根据文件md5值找到原文件信息，文件ID、文件名、文件类型、文件大小、上传日期
    public File getFileInfoByRowkey(String md5) {}
    // 向file表中添加文件的信息
    public String updateFileCurrentChunkAndTotalChunks(File file) {}
    // file表按行数计算文件ID
    public String getFileIdByTableCount() {}

    // 向fileMD5表中添加文件的信息
    public String addFileMD5(File file) {}

    // 根据文件ID找到文件MD5值、文件大小
    public File_MD5 getFileMD5ById(String id) {}
```

##### 	-- Init.java				初始化数据库操作

```java
	// 创建表
    public void createTable(String tableName, String[] family) throws IOException {}

    // 创建禁用文件信息
    public void createBanFile(String type){}
```

##### 	-- MailDao.java		邮件表数据库操作

```java
    // 添加邮件信息
    public void createMail(Mail mail){}

    // 根据邮件链接的md5值找到邮件信息
    public Mail getMailInfoById(String mail_md5) {}

    // 删除邮件信息
    public void delMailInfoById(String mail_md5){}

    // 添加邮箱用户信息（邮箱与用户绑定，一个邮箱只能注册一个账号）
    public void createMailUser(Mail_User mail_user){}

    // 根据邮箱查找邮箱用户信息
    public Mail_User getMailUserInfoById(String mail) {}

    // 删除邮箱用户信息
    public void delMailUserInfoById(String mail){}
```

##### 	-- ShareDao.java		分享表数据库操作

```java
    // 添加分享信息
    public void createShare(Share share){}

    /根据分享链接的md5值找到文件夹ids
    public Share getShareInfoById(String share_md5) {}

    // 删除分享
    public void delShareInfoById(String share_md5){}
```

##### 	-- UserDao.java		用户表数据库操作

```java
    // 修改用户或添加用户基本信息
    public void userInfo(User user){}

    // 根据用户ID找到用户基本信息
    public User getUserInfoById(String id) {}

    // 删除用户所有信息
    public void delUserInfoById(String id){}

    // 根据用户ID找到HostHolder用户的关键信息
    public User getHostHolderUserById(String userId) {}

    // 添加用户文件信息（文件ID与对应的数目）
    public void addUserFile(User user){}

    // 获得用户文件数目
    public String getUserFileCount(String userId, String fileId){}

    // 删除用户文件信息（文件数目-1，为1时删除该条文件信息）
    public void delUserFile(User user){}

    // 得到用户所有文件id、空间信息（用于计算用户剩余空间）
    public User getUserFilesAndSpace(String id){}

    // 添加用户文件夹信息
    public void addUserDir(User user){}

    // 删除用户文件夹信息
    public void delUserDir(User user){}

    // 得到用户所有文件夹id
    public User getUserDirs(String userId){}

    // 添加用户分享信息
    public void addUserShare(User user){}

    // 删除用户分享信息
    public void delUserShare(User user){}

    // 得到用户所有分享链接
    public User getUserShares(String userId){}

    // 添加用户空间信息
    public void addUserSpace(User user){}

    // 只添加用户总空间信息
    public void addUserTotalSpace(String userId, String totalSpace){}

    // 得到用户空间信息
    public User getUserSpace(String userId){}
```

#### - entity		对象

##### 	-- Admin.java				管理员

```java
public class Admin {
    private String id;			管理员ID
    private String pwd;			密码
    private String phone;		电话
    private String level;		等级（common、operate、admin）
}
```

##### 	-- Analysis.java			分析

```java
public class Analysis {
    private String rowKey;		数据类型_操作类型_ID_日期
    private String count;		计数
}
```

##### 	-- Dir.java						文件夹

```java
public class Dir {
    private String dir_id;			文件夹ID
    private String dir_name;		文件夹名称
    private String parent_id;		父文件夹ID
    private String sub_ids;			子文件夹IDs
    private String path;			路径
    private String date;			创建日期
    private String isDir;			是否为文件夹

    // isDir不为文件夹时
    private String file_id;			对应文件ID
    private String file_size;		文件大小

    // 显示图标使用（不存入数据库）
    public String file_type;
}
```

##### 	-- File.java					文件

```java
public class File {
    // File_Info
    private String md5;				md5值
    private String file_id;			文件ID
    private String file_name;		文件名
    private String file_type;		文件类型（后缀）
    private long size;				大小
    private String currentChunk;	当前块
    private String totalChunks;		总块数
    private long date;				上传完成日期
}
```

##### 	-- File_MD5.java		文件ID对应MD5

```java
public class File_MD5 {
    private String file_id;		文件ID
    private String md5;			文件对应MD5
    private String size;		大小
}
```

##### 	-- LoginTicket.java	登录凭证

```java
public class LoginTicket {
    private String ticket;		凭证ID（md5）
    private String userId;		用户ID
    private String status;		状态（有效、无效）
    private String expired;		超时时间
}
```

##### 	-- Mail.java					邮箱

```java
public class Mail {
    private String mail_md5;	邮箱链接md5
    private String mail;		邮箱
    private String user_id;		用户ID
    private String action;		行为（激活、解绑、忘记密码）
    private String expired;		超时时间
    private String content;		状态
}
```

##### 	-- Mail_User.java		邮箱用户绑定

```java
public class Mail_User {
    private String mail;		邮箱
    private String userId;		用户ID
}
```

##### 	-- Share.java				分享

```java
public class Share {
    private String share_md5;	分享链接（md5）
    private String user_id;		分享者用户ID
    private String dir_ids;		分享文件夹IDs
    private String key;			口令
    private String expired;		超时时间
    private String status;		状态
}
```

##### 	-- User.java				用户

```java
package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("user")
public class User {
    // User_Info
    private String id; // 学号
    private String pwd; // 密码
    private String name; // 姓名
    private String sex; // 性别
    private String institute; // 学院
    private String grade; // 年级
    private String major; // 专业
    private String date; // 生日
    private String phone; // 电话
    private String address; // 地址
    private String mail; // 邮箱
    private String status; // 状态（是否已激活）

    // User_File
    private String file_id; //  文件ID
    private String file_count; // 	文件ID对应文件数量
    private String[] file_ids; //  获取到用户所有文件ID

    // User_Dir
    private String dir_id;
    private String[] dir_ids; //  获取到用户所有文件夹ID

    // User_Share
    private String share_md5;    // 分享链接的MD5
    private String[] share_md5s; //  获取到用户所有分享链接

    // User_Space
    private String use_space; // 已用空间（B）
    private String surplus_space;// 剩余空间（B）
    private String totalSpace;// 总空间2GB
}
```

#### - service		服务接口实现

##### 	-- impl		具体实现

###### 		--- AdminServiceImpl.java					管理员具体实现

###### 		--- FileServiceImpl.java						文件具体实现

###### 		--- LoginTicketServiceImpl.java		登录凭证具体实现

###### 		--- UserServiceImpl.java					用户具体实现

##### 	-- AdminService.java

```java
public interface AdminService {
    // 检查ID是否已经被占用
    public boolean checkId(String id);
    // 添加管理员
    public void addAdmin(Admin admin);
    
    // 分析数据
    // 根据Choice分析数
    public List<Analysis> analysisData(String choice);
    // 根据Choice和日期分析数据
    public List<Analysis> analysisDataByDate(String choice, String startDate, String endDate);
    // 每天的数据
    public List<List<String>> analysisDay(String choice, String startDate, String endDate);
    // 排行数据（用于echarts显示）
    public List<List<String>> analysisTopDataByDate(String choice, int top, String startDate, String endDate);
    // 排行数据（Table显示数据）
    public List<String> analysisTopLineByDate(String choice, int top, String startDate, String endDate);
    
    // 查找所有用户
    public List<User> findAllUser();
    // 查找管理员信息（比自己低一级别的）
    public List<Admin> findAllAdmin(String level);
    // 查找所有文件
    public List<File> findAllFile();
    // 查找所有被禁用的文件
    public List<File> findAllBanFile();
    // 查找所有分享
    public List<Share> findAllShare();
    // 删除用户及其关联的信息
    public void delUserAllInfo(String userId);
    // 和谐资源,资源是否备份,新名字
    public void banFile(String banFileId, String destFileId, boolean flag, String newName);
    // 和谐分享链接
    public void banShare(String shareMd5);
}
```

##### 	-- FileService.java

```java
public interface FileService {
    // 获得文件夹列表，查看文件或目录列表
    public List<Dir> getDirOrFileList(User user, String parentId, String type, String upOrDown);
    // 获得文件列表，查看文件或目录列表
    public List<Dir> getUserFileList(User user, String fileType, String type, String upOrDown);
    // 获得文件列表，查看文件或目录列表
    public List<Share> getUserShareList(User user, String upOrDown);
    // 添加dir文件类型
    public void addDirFileType(Dir dir);
    // 判断文件夹是否已存在
    public boolean isFolderRepeat(String parentId, String path);
    // 上传文件
    public String uploadFile(InputStream inputStream, String md5);
    // 用户生成上传文件信息
    public String uploadFileInfo(String userId, String parentId, String md5);
    // 创建文件夹
    public boolean makeFolder(String userId, Dir dir, String parentid);
    // 移动文件夹
    public String moveFolder(String userId, String parentId, String dirId);
    // 复制或保存文件夹
    public String copyOrSaveFolder(String userId, String parentId, String dirId, String type);
    // 删除文件夹
    public void deleteFolder(User user, Dir dir);
    // 文件夹或重命名
    public String rename(String userId, String dirId, String newName);
    // 下载文件
    public boolean downloadFile(Dir dir, String local);
    //下载文件夹（里面可包含多个文件）
    public void downloadDir(User user, Dir dir, String rootPC, String path);
    // 分享文件
    public String shareDirs(String userId, String dirIds, String choice);
}
```

##### 	-- LoginTicketService.java

```java
public interface LoginTicketService {
    // 插入凭证数据
    int insertLoginTicket(LoginTicket loginTicket);
    // 查找凭证数据
    LoginTicket selectByTicket(String ticket);
    // 更新凭证状态
    int updateStatus(String ticket, String status);
}
```

##### 	-- UserService.java

```java
public interface UserService {
    // 检查ID是否已经被占用
    public boolean checkId(String id);
    // 检查邮箱是否已被占用
    public boolean checkMail(String mail);
    // 添加用户信息到数据库（密码加密）
    public void addUser(User user);
    // 检查用户信息是否完整
    public boolean checkUserInfo(User user);
    // 登录操作
    public Map<String, Object> login(String userId, String password, int expiredSeconds);
    // 初始化用户根目录文件夹及信息
    public void initDir(User user);
    // 计算用户空间信息
    public void calSpace(String userId);
    // 退出登录
    public void logout(String ticket);
    // 查找登录凭证
    public LoginTicket fineLoginTicket(String ticket);
}
```

#### - util		封装的方法

##### 	-- Constants.java		自定义变量

```java
public class Constants {
    // 文件后缀
    // 文档
    public static final List<String> DOCUMENT_SUFFIX = Arrays.asList("csv","txt","doc","docx","xls","xlsx","ppt","pptx","pdf");
    // 图片
    public static final List<String> IMG_SUFFIX = Arrays.asList("jpg","jpeg","gif","bmp","png");
    // 视频
    public static final List<String> VIDEO_SUFFIX = Arrays.asList("avi","mp4","mp3","rmvb","flv","m4a");
    // 代码
    public static final List<String> CODE_SUFFIX = Arrays.asList("c","cpp","java","py","xml","html","php","css","js","h");
    // 其他
    public static final List<String> OTHER_SUFFIX = Arrays.asList("apk","exe","swf","torrent","zip");

    // 网盘空间大小B
    // 500MB    注册信息不完善
    public static final String SMALL_TOTAL_SPACE = "524288000";
    // 2GB      信息完善普通用户
    public static final String DEFAULT_TOTAL_SPACE = "2147483648";
    // 5GB      VIP？
    public static final String MIDDLE_TOTAL_SPACE = "5368709120";
    // 20GB     SVIP？
    public static final String LARGE_TOTAL_SPACE = "21474836480";

    // 用户表
    public static final String TABLE_USER = "user";
    // 用户信息列
    public static final String FAMILY_USER_INFO = "user_info";
    public static final String[] COLUMN_USER_INFO = { "pwd", "name", "sex", "institute", "grade", "major", "date", "phone", "address", "email", "status" };
    // 用户文件列
    public static final String FAMILY_USER_FILE = "user_file";
    // 用户文件夹列
    public static final String FAMILY_USER_DIR = "user_dir";
    // 用户分享列
    public static final String FAMILY_USER_SHARE = "user_share";
    // 用户空间列
    public static final String FAMILY_USER_SPACE = "user_space";
    public static final String[] COLUMN_USER_SPACE = { "use_space", "surplus_space", "total_space"};
    // 用户表列簇名
    public static final String[] FAMILY_USER = {FAMILY_USER_INFO, FAMILY_USER_FILE, FAMILY_USER_DIR, FAMILY_USER_SHARE, FAMILY_USER_SPACE};
//    public static final String FAMILY_USER_FRIEND = "user_friend";
//    public static final String[] COLUMN_USER_FRIEND = { "friend_id", "friend_name" };
    public static final String[] INSTITUTE = {"", "机械工程学院", "电子信息与电气工程学院", "计算机科学与信息工程学院", "土木与建筑工程学院", "化学与环境工程学院", "生物与食品工程学院", "经济管理学院", "文法学院", "外国语学院", "艺术设计学院", "数理学院","国际教育学院", "飞行学院"};

    // 邮件表
    public static final String TABLE_MAIL = "mail";
    public static final String FAMILY_MAIL_INFO = "mail_info";
    public static final String[] COLUMN_MAIL_INFO = {"mail", "user_id", "action", "expired", "content"};
    // 邮件表列簇
    public static final String[] FAMILY_MAIL = {FAMILY_MAIL_INFO};

    // 邮箱用户表
    public static final String TABLE_MAIL_USER = "mail_user";
    public static final String FAMILY_MAIL_USER_INFO = "mail_user_info";
    public static final String COLUMN_MAIL_USER_INFO = "user_id";
    // 邮箱用户表列簇
    public static final String[] FAMILY_MAIL_USER = {FAMILY_MAIL_USER_INFO};


    // 文件表
    public static final String TABLE_FILE = "file";
    public static final String FAMILY_FILE_INFO = "file_info";
    public static final String[] COLUMN_FILE_INFO = { "file_id", "file_name", "file_type", "size", "currentChunk", "totalChunks", "date" };
    // 文件表列簇
    public static final String[] FAMILY_FILE = {FAMILY_FILE_INFO};

    // 文件ID与其MD5值关联表
    public static final String TABLE_FILEMD5 = "file_MD5";
    public static final String FAMILY_FILEMD5_INFO = "MD5_info";
    public static final String[] COLUMN_FILEMD5_INFO = { "MD5", "size" };
    // 文件ID与其MD5值关联表列簇
    public static final String[] FAMILY_FILEMD5 = {FAMILY_FILEMD5_INFO};

    // 文件夹表
    public static final String TABLE_DIR = "dir";
    // 文件夹信息列
    public static final String FAMILY_DIR_INFO = "dir_info";
    public static final String[] COLUMN_DIR_INFO = { "dir_name", "parent_id", "sub_id", "path", "dir_date", "isDir" };
    // 文件夹的文件信息列
    public static final String FAMILY_DIRFILE_INFO = "dir_file";
    public static final String[] COLUMN_DIRFILE_INFO = { "file_id", "file_size" };
    // 文件夹表列簇
    public static final String[] FAMILY_DIR = {FAMILY_DIR_INFO, FAMILY_DIRFILE_INFO};

    // cookie过期时间
    // 12小时
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    // 100天
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    // 登录凭证表
    public static final String TABLE_TICKET = "ticket";
    public static final String FAMILY_TICKET_INFO = "ticket_info";
    public static final String[] COLUMN_TICKET_INFO = { "user_id", "status", "expired" };
    // 登录凭证表列簇
    public static final String[] FAMILY_TICKET = {FAMILY_TICKET_INFO};

    // 分享失效时间
    // 1天、7天、30天、永久
    public static final Long DAY = 3600 * 24l;
    public static final Long WEEK = 3600 * 24 * 7l;
    public static final Long MONTH = 3600 *24 * 30l;
    public static final String FOREVER = "forever";
    // 分享表
    public static final String TABLE_SHARE = "share";
    public static final String FAMILY_SHARE_INFO = "share_info";
    public static final String[] COLUMN_SHARE_INFO = { "user_id", "dir_ids", "key", "expired" , "status" };
    // 分享表列簇
    public static final String[] FAMILY_SHARE = {FAMILY_SHARE_INFO};

    // 管理员表
    public static final String TABLE_ADMIN = "Admin";
    public static final String FAMILY_ADMIN_INFO = "info";
    public static final String[] COLUMN_ADMIN_INFO = { "phone", "pwd", "level"};
    // 管理员列簇
    public static final String[] FAMILY_ADMIN = {FAMILY_ADMIN_INFO};

    // 分析数据表
    public static final String TABLE_ANALYSIS = "analysis";
    public static final String FAMILY_ANALYSIS_INFO = "analysis_info";
    public static final String COLUMN_ANALYSIS_INFO = "count";
    // 分析数据表列簇
    public static final String[] FAMILY_ANALYSIS = {FAMILY_ANALYSIS_INFO};
}
```

##### 	-- CookieUtil.java		Cookie凭证方法

```java
// 获取Cookie对应name的值
public static String getValue(HttpServletRequest request, String name)
```

##### 	-- DateUtil.java		日期方法

```java
	// long型时间戳转化为对应格式的时间字符串
	public static String longToString(String dateFormat,Long millSec) {}
	// Date型日期转换为对应格式的时间字符串
	public static String DateToString(String dateFormat,Date date) {}
	// 时间字符串转换为Long行时间戳
	public static Long StringToLong(String time){}
```

##### 	-- FilesUtil.java		文件方法

```java
package com.example.d.util;

import java.text.DecimalFormat;

public class FilesUtil {
    //	格式化文件大小(long型B转化为KB、MB、GB)
    public static String FormatFileSize(long fileS) {}
    // 比较文件大小（B、KB、MB、GB）
    public static int compareSize(String size1, String size2){}
    // 单位比重（0:B、1:KB、2:MB、2:GB）
    public final static int getSizeUnit(String size){}
    // 获得文件名前缀
    public static String getFilePrefix(String fileName) {}
    // 获得文件名后缀
    public static String getFileSuffix(String fileName){}
```

##### 	-- HostHolder.java		当前用户方法

```java
/**
 * 持有用户或管理员信息，用于代替session对象
 */
@Component
public class HostHolder {
    // User线程
    private ThreadLocal<User> users = new ThreadLocal<>();
    // 设置用户
    public void setUser(User user){users.set(user);}
    // 获取用户
    public User getUser(){ return users.get(); }
    // 清除用户
    public void clear(){ users.remove(); }
    // 管理员线程
    private ThreadLocal<Admin> admins = new ThreadLocal<>();
    // 设置管理员
    public void setAdmin(Admin admin){admins.set(admin);}
    // 获取管理员
    public Admin getAdmin(){ return admins.get(); }
    // 清除管理员
    public void clearAdmin(){ admins.remove(); }
}
```

##### 	-- MailClient.java		邮件方法

```java
	// 设置发送邮件的邮箱地址
	@Value("${spring.mail.username}")
    private String from;

    // 发送邮件
    public void sendMail(String to, String subject, String content){}
```

##### 	-- MD5Util.java		MD5方法

```java
    // 盐
    private static final String staticSalt = "onlinedisk";

    /**
     * 计算输入流的MD5值
     * @param in
     * @return MD5字符串
     */
    public static String getFileMD5(InputStream in) {}

    /**
     * 根据用户id和用户输入的原始密码，进行MD5加密
     * @param userPwd
     * @return 加密后的字符串
     */
    public static String encodePwd(String userPwd) {}

    /**
     * 判断用户输入的密码是否正确
     * @param userPwd：当前输入的密码
     * @param dbPwd：数据库中存储的密码
     * @return true:输入正确 false：输入错误
     */
    public static boolean isPwdRight(String userPwd, String dbPwd) {}
```

##### 	-- PinYinUtil.java		拼音方法

```java
    /**
     * @param china (字符串 汉字)
     * @return 汉字转拼音 其它字符不变
     */
    public static String getPinyin(String china){}
```

##### 	-- UserUtil.java		用户方法

```java
    // 计算空间剩余大小
    public static void countSurplusSpace(User user){}
    // 可获得列簇内所有的key值（IDs）和对应的value值（文件ID的count）
    // 只获得列簇内所有的key值（IDs）
    public static String[] getColumnsByResult(Result result, String family){}
```

#### - Application.java		运行代码

### 前端Html代码

#### - admin		管理员页面文件夹

##### 			-- analysis		分析页面

###### 					--- analysisDay.html		每日分析数据页面

###### 					--- analysisShare.html		分享分析数据页面

###### 					--- analysisTop.html		排行分析数据页面

##### 			-- info		详细信息页面

###### 					--- fileInfo.html		文件详细信息页面

###### 					--- userInfo.html		用户详细信息页面

##### 				-- list		数据列表页面

###### 					--- banFileList.html		被禁用文件列表页面

###### 					--- fileList.html				文件列表页面

###### 					--- shareList.html			分享列表页面

###### 					--- userList.html				用户列表页面

##### 			-- addInfo.html		添加用户或管理员信息页面

##### 			-- admin.html		管理员列表及操作结果页面

##### 		-- adminLogin.html		管理员登录页面

##### 			-- edit.html		编辑用户、管理员信息页面

#### 	-  mail						邮箱内容页面

##### 			-- mailBound.html			绑定邮箱邮件

##### 			-- mailForget.html			忘记密码邮件

##### 			-- mailRegister.html		注册页面

##### 			-- mailUnbound.html		解绑邮箱页面

### - user							用户页面文件夹

##### 			-- changePwd.html					修改密码页面

##### 			-- copyOrMoveOrSave.html	复制、移动、保存（分享链接保存）页面

##### 			-- editInfo.html							更改用户信息页面

##### 			-- forgot.html								忘记密码页面

##### 			-- index.html									用户主页面（显示内容）

##### 			-- share.html									分享内容页面

##### 			-- shareVerify.html						分享链接认证页面

### - close.html					关闭页面

### - error.html					错误页面

### - index.html					登录主页面

### - login.html					用户登录页面

### - wait.html						等待跳转页面

### - waitClose.html				等待关闭页面

### 前端JS代码

### - jquery-2.1.4.min.js			jquery包

### - pan-admin-function.js	管理员功能方法

```js
// 选择框
/* 是否点击全选*/
$('input[name="allboxs"]').click(function() {});
// 按钮点击
function clickBoxs(){}

// 添加用户或管理员
function addUserOrAdmin(type) {}

// 用户或文件详细信息
function openInfo(type, id) {}

// 管理员编辑（用户或管理员）弹窗
function openEdit(type, id){};

// 管理员禁用文件或分享链接
function ban(type, flag, id) {}

// 管理员下载文件
function downloadFile(md5) {}

// 打开分享链接
function openShare(share_md5) {}
// 复制接收到的分享链接信息到剪贴板
function copyShare(share_md5, key) {}
```

### - pan-admin-list.js			管理员显示列表JS方法

```js
// 向后端请求数据
// 用户列表数据
function allListUsers(){}
// 文件列表数据
function allListFiles(){}
// 被禁用文件列表数据
function allListBanFiles(){}
// 分享链接数据
function allListShares(){}

// 获取数据
var datas;
function getDatas(Datas){}

/**
 * 初始化layui分页
 */
function initLayPage(choice, pageConf) {}

// 分页内容改变
function changeLayPage(choice, data, pageConf){}


//填充用户表格数据
function fillUsersTable(data, start, end) {};
//填充文件表格数据
function fillFilesTable(data, start, end) {};
//填充被禁用文件表格数据
function fillBanFilesTable(data, start, end) {};
//填充分享表格数据
function fillSharesTable(data, start, end) {};

// 正则搜索
function searchByReg(datas, keyWord, type){}

// 排序
function sortBy(choice, type, upOrDown, id){}
// 汉字转拼音
function CC2PY(l1) {}
// 在对象中搜索
function arraySearch(l1,l2){}
// size（B、KB、MB、GB）比较大小
function compareSize(size1, size2){}
// 获取size单位级别
function getSizeUnit(size){}
```

### - pan-user-function.js			用户JS方法

```js
// 修改信息弹窗
function editInfo(){};

// 更改密码弹窗
function changePwd(){};

// 新建文件夹按钮触发
function mkdir(parentId, userId) {}

// 分享按钮触发
function choiceShare(dirId) {}
// 发送要分享的数据
function share(dirIds, choice) {}
// 打开分享链接
function openShare(share_md5) {}
// 复制接收到的分享链接信息到剪贴板
function copyShare(share_md5, key) {}

// 移动、复制、（分享链接内保存）按钮触发
function copyOrMoveOrSaveDirs(type, userId) {}

// 删除按钮触发
function deleteDirs(dirId, parentId, userId) {};

// 选择框
/* 是否点击全选*/
$('input[name="allboxs"]').click(function() {});
/*判断当全选时 若有一个不选 全选按钮为不选状态*/
$('input[name="boxs"]').click(function() {});

// 重命名按钮触发
function rename(dirId, dirName) {}

// 下载按钮触发
function downloadFile(dirId, dirName) {}

// 保存按钮触发弹出选择保存位置
function openSave(type, dirId, userId) {}
// 保存弹窗内的保存操作
function save(dirIds, userId, parentId, type) {}
// 关闭弹窗
function cancle() {}
```

### - Pinyin.js					汉字转拼音使用

### - spark-md5.js			md5加密使用