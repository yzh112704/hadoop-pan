package com.example.d.controller;

import com.example.d.dao.*;
import com.example.d.entity.*;
import com.example.d.entity.File;
import com.example.d.service.FileService;
import com.example.d.service.LoginTicketService;
import com.example.d.service.UserService;
import com.example.d.util.DateUtil;
import com.example.d.util.FilesUtil;
import com.example.d.util.HostHolder;
import com.example.d.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class FileController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private DirDao dirDao;
    @Autowired
    private ShareDao shareDao;
    @Autowired
    private AnalysisDao analysisDao;

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
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown){
        User user = hostHolder.getUser();
        String userId = user.getId();

        user = userDao.getUserDirs(userId);
        user.setId(userId);
        List<Dir> dirs = fileService.getDirOrFileList(user, dirId, type, upOrDown);
        Dir dir = dirDao.getDirInfoById(dirId);

        if(upOrDown.equals("up"))
            upOrDown = "down";
        else
            upOrDown = "up";
        model.addAttribute("type", type);
        model.addAttribute("upOrDown", upOrDown);
        model.addAttribute("path", "我的文件" + dir.getPath());
        model.addAttribute("dirs", dirs);
        model.addAttribute("parentId", dirId);
        return "user/index";
    }

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
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown) {
        User user = hostHolder.getUser();
        String userId = user.getId();
        user = userDao.getUserDirs(userId);
        user.setId(userId);

        Dir dir = dirDao.getDirInfoById(parentId);
        List<Dir> dirs = fileService.getDirOrFileList(user, dir.getParent_id(), type, upOrDown);
        if(dir.getParent_id().equals("null"))
            model.addAttribute("path", "我的文件");
        else
            model.addAttribute("path", "我的文件" + dirDao.getDirInfoById(dir.getParent_id()).getPath());
        model.addAttribute("dirs", dirs);
        model.addAttribute("parentId", dir.getParent_id());
        model.addAttribute("type", type);
        if(upOrDown.equals("up"))
            upOrDown = "down";
        else
            upOrDown = "up";
        model.addAttribute("upOrDown", upOrDown);
        return "user/index";
    }

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
            , @RequestParam(value = "upOrDown", defaultValue = "up") String upOrDown) {
        User user = hostHolder.getUser();
        String userId = user.getId();

        user = userDao.getUserDirs(userId);
        user.setId(userId);

        if(fileType.equals("all")){
            List<Dir> dirs = fileService.getDirOrFileList(user, userId, type, upOrDown);
            model.addAttribute("path", "我的文件");
            model.addAttribute("dirs", dirs);
            model.addAttribute("parentId", userId);
        }
        else if(fileType.equals("share")){
            user = userDao.getUserShares(userId);
            List<Share> shares = fileService.getUserShareList(user, upOrDown);
            model.addAttribute("shares", shares);
            model.addAttribute("path", "我的分享");
            model.addAttribute("parentId", "null");
        }
        else{
            List<Dir> dirs = fileService.getUserFileList(user, fileType, type, upOrDown);
            if(fileType.equals("img"))
                model.addAttribute("path", "我的图片");
            else if(fileType.equals("document"))
                model.addAttribute("path", "我的文档");
            else if(fileType.equals("video"))
                model.addAttribute("path", "我的视频");
            else if(fileType.equals("other"))
                model.addAttribute("path", "其他");
            model.addAttribute("dirs", dirs);
            model.addAttribute("parentId", "null");
        }
        model.addAttribute("type", type);
        if(upOrDown.equals("up"))
            upOrDown = "down";
        else
            upOrDown = "up";
        model.addAttribute("fileType", fileType);
        model.addAttribute("upOrDown", upOrDown);
        return "user/index";
    }

    /**
     * 检查文件
     * 1、文件是否存在     不存在新建，存在执行2、3步
     * 2、文件大小是否超出限制（1GB）
     * 3、当前已经上传的块数
     * @param md5           文件MD5
     * @param fileName      文件名
     * @return
     */
    @RequestMapping(path = "/checkFile", method = RequestMethod.POST)
    @ResponseBody
    public String checkFile(@RequestParam("md5") String md5
            ,@RequestParam("fileName") String fileName){
        File file = fileDao.getFileInfoByRowkey(md5);
        if(file == null){
            // 配置文件信息
            file = new File();
            file.setMd5(md5);
            file.setFile_name(fileName);
            file.setFile_id(fileDao.getFileIdByTableCount());
            file.setFile_type(FilesUtil.getFileSuffix(fileName));
            // File表添加文件信息
            fileDao.addFileInfo(file);
            return "0";
        }
        else if(file.getCurrentChunk() == null){
            file.setCurrentChunk("1");
            fileDao.addFileInfo(file);
            return "0";
        }
        else if(file.getCurrentChunk() != null && file.getTotalChunks()!=null){
            if(Integer.valueOf(file.getTotalChunks()) >= 1024){
                return "big";
            }
            if(Long.valueOf(file.getCurrentChunk()) < Long.valueOf(file.getTotalChunks()))
                return String.valueOf(Long.valueOf(file.getCurrentChunk()) - 1);
        }
        return "exist";
    }

    /**
     * 上传文件
     * @param md5               上传文件md5
     * @param currentChunk      当前块编号
     * @param totalChunks       总块数
     * @param data              块内容
     * @param request           用户获取路径
     * @return
     */
    @RequestMapping(path = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("md5") String md5
            , @RequestParam("currentChunk") String currentChunk
            , @RequestParam("totalChunks") String totalChunks
            , @RequestParam("data") MultipartFile data
            , HttpServletRequest request){
        if(Integer.valueOf(totalChunks) >= 1024){
            File file = new File();
            file.setMd5(md5);
            file.setTotalChunks(totalChunks);
            fileDao.updateFileCurrentChunkAndTotalChunks(file);
            return "big";
        }
        String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
        java.io.File localDir = new java.io.File(uploadPath);
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        String uploadFile = uploadPath + "/" + md5;
        try{
            FileOutputStream fos= new FileOutputStream(uploadFile,true);
            fos.write(data.getBytes());
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
            return "error";
        }
        File file = new File();
        file.setMd5(md5);
        file.setCurrentChunk(currentChunk);
        file.setTotalChunks(totalChunks);
        fileDao.updateFileCurrentChunkAndTotalChunks(file);
        if(currentChunk.equals(totalChunks)) {
            try{
                return fileService.uploadFile(new FileInputStream(uploadFile), md5);
            }catch (IOException e){
                e.printStackTrace();
                return "error";
            }
        }
        return "success";
    }

    /**
     * 数据库上传文件信息
     * @param userId        用户ID
     * @param parentId      上传到父文件夹ID内
     * @param md5           文件md5
     * @return
     */
    @RequestMapping(path = "/uploadFileInfo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileInfo(@RequestParam(value = "userId") String userId
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "md5") String md5){
        String code = fileService.uploadFileInfo(userId, parentId, md5);
        userService.calSpace(userId);
        return code;
    }

    /**
     * 新建文件夹
     * @param dirName       文件夹名
     * @param parentId      父文件夹ID
     * @param userId        用户ID
     * @return
     */
    @RequestMapping(path = "/makeDir", method = RequestMethod.POST)
    @ResponseBody
    public String makeDir(@RequestParam(value = "dirName") String dirName
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "userId") String userId){
        Dir dir = new Dir();
        dir.setDir_name(dirName);
        dir.setIsDir("true");
        return String.valueOf(fileService.makeFolder(userId, dir, parentId));
    }

    /**
     * 重命名文件夹或文件
     * @param dirId         要重命名的文件夹或文件ID
     * @param newName       新名字
     * @return
     */
    @RequestMapping(path = "/rename", method = RequestMethod.POST)
    @ResponseBody
    public String rename(@RequestParam(value = "dirId") String dirId
            , @RequestParam(value = "newName") String newName){
        return fileService.rename(hostHolder.getUser().getId(), dirId, newName);
    }

    /**
     * 下载文件
     * @param request   获取请求的路径
     * @param dirId     要下载的文件ID（文件夹ID）
     * @return
     */
    @RequestMapping(path = "/downloadFile", method = RequestMethod.POST)
    @ResponseBody
    public String downloadFile(HttpServletRequest request
            , @RequestParam(value = "dirId", defaultValue = "null") String dirId){
        String downloadPath = request.getSession().getServletContext().getRealPath("/downloadFile/");
        java.io.File localDir = new java.io.File(downloadPath);
        if (!localDir.exists()) {
            localDir.mkdir();
        }
        Dir dir = dirDao.getDirInfoById(dirId);
        fileService.downloadFile(dir, downloadPath);
        String url = "downloadFile/" + dir.getDir_name();
        // 生成并添加分析信息
        // 用户下载次数
        Analysis analysis = new Analysis();
        String date = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
        analysis.setRowKey("user_download_" + hostHolder.getUser().getId() + "_" + date);
        analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
        analysisDao.addAnalysis(analysis);
        // 文件被下载次数
        analysis.setRowKey("file_download_" + dir.getFile_id() + "_" + date);
        analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
        analysisDao.addAnalysis(analysis);
        return url;
    }

    /**
     * 删除文件夹
     * @param dirIds    要删除的文件夹IDs
     * @param userId    用户ID
     * @return
     */
    @RequestMapping(path = "/deleteDirs", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId){
        User user = userDao.getUserDirs(userId);
        user.setId(userId);
        for(String dirId: dirIds.split(" ")) {
            Dir dir = dirDao.getDirInfoById(dirId);
            fileService.deleteFolder(user, dir);
        }
        userService.calSpace(userId);
        return "1";
    }

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
            , @RequestParam(value = "type") String type){
        User user = userDao.getUserDirs(userId);
        List<Dir> dirs = fileService.getDirOrFileList(user, dirId, "name", "down");
        Dir dir = dirDao.getDirInfoById(dirId);

        model.addAttribute("userId", userId);
        model.addAttribute("path", "我的文件" + dir.getPath());
        model.addAttribute("dirs", dirs);
        model.addAttribute("parentId", dirId);
        model.addAttribute("dirIds", dirIds);
        model.addAttribute("type", type);
        return "user/copyOrMoveOrSave";
    }

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
            , @RequestParam(value = "type") String type){
        User user = userDao.getUserDirs(userId);
        Dir dir = dirDao.getDirInfoById(dirId);

        List<Dir> dirs = fileService.getDirOrFileList(user, dir.getParent_id(), "name", "down");
        if(dir.getParent_id() == "null")
            model.addAttribute("path", "我的文件");
        else
            model.addAttribute("path", "我的文件" + dirDao.getDirInfoById(dir.getParent_id()).getPath());
        model.addAttribute("dirs", dirs);
        model.addAttribute("parentId", dir.getParent_id());
        model.addAttribute("dirIds", dirIds);
        model.addAttribute("type", type);
        model.addAttribute("userId", userId);
        return "user/copyOrMoveOrSave";
    }

    /**
     * 移动或复制或保存文件夹
     * @param dirIds        要移动或复制或保存的文件夹IDs
     * @param userId        用户ID
     * @param parentId      父文件夹ID
     * @param type          类型（copy复制、move移动、save保存）
     * @return
     */
    @RequestMapping(path = "/copyOrMoveOrSaveDirs", method = RequestMethod.POST)
    @ResponseBody
    public String copyOrMoveOrSaveDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "userId") String userId
            , @RequestParam(value = "parentId") String parentId
            , @RequestParam(value = "type") String type){
        String flag = "";
        if(type.equals("move")){
            for(String dirId: dirIds.split(" ")){
                flag = String.valueOf(fileService.moveFolder(userId, parentId, dirId));
                // 有一个文件夹移动不成功，直接返回错误
                if(!flag.equals("moveSuccess"))
                    return flag;
            }
        }
        else if(type.equals("copy")){
            for(String dirId: dirIds.split(" ")) {
                flag = fileService.copyOrSaveFolder(userId, parentId, dirId, type);
                // 有一个文件夹复制不成功，直接返回错误
                if(!flag.equals("copySuccess"))
                    return flag;
                userService.calSpace(userId);
            }
        }
        else if(type.equals("save")){
            for(String dirId: dirIds.split(" ")) {
                flag = fileService.copyOrSaveFolder(userId, parentId, dirId, type);
                // 有一个文件夹保存不成功，直接返回错误
                if(!flag.equals("saveSuccess"))
                    return flag;
                userService.calSpace(userId);
            }
        }
        return flag;
    }

    /**
     * 分享文件夹
     * @param dirIds        要分享的文件IDs
     * @param choice        选择的分享时效（1天、1周、1月、永久）
     * @return  url         生成的链接部分信息
     */
    @RequestMapping(path = "/shareDirs", method = RequestMethod.POST)
    @ResponseBody
    public String shareDirs(@RequestParam(value = "dirIds") String dirIds
            , @RequestParam(value = "choice") String choice){
        String userId = hostHolder.getUser().getId();
        return fileService.shareDirs(userId, dirIds, choice);
    }

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
            , HttpServletResponse response){
        share_md5 = share_md5.split("口令")[0];
        // 查询分享内容信息
        Share share = shareDao.getShareInfoById(share_md5);
        // 分享是否存在
        if(share == null)
            return "error";
        model.addAttribute("shareUserId", share.getUser_id());
        // 链接是否被取消
        if(share.getStatus().equals("false")){
            model.addAttribute("code", "expired");
            return "user/shareVerify";
        }
        // 判断链接时间不是永久
        if(!share.getExpired().equals("forever")) {
            if (Long.valueOf(share.getExpired()) < System.currentTimeMillis()){
                // 链接失效直接返回认证页面（提示链接已失效）
                model.addAttribute("code", "expired");
                return "user/shareVerify";
            }
        }
        model.addAttribute("share_md5", share_md5);
        User loginUser = hostHolder.getUser();

        Admin loginAdmin = hostHolder.getAdmin();
        if(loginAdmin == null){
            if(loginUser == null){
                model.addAttribute("content", "content");
                model.addAttribute("middleResult", "请先登录账号！");
                return "login";
            }
            else if(share.getUser_id().equals(loginUser.getId())){
                List<Dir> dirs = new ArrayList<>();
                for(String dirId: share.getDir_ids().split(" ")){
                    Dir subDir = dirDao.getDirInfoById(dirId);
                    if(!subDir.getIsDir().equals("")) {
                        fileService.addDirFileType(subDir);
                        dirs.add(subDir);
                    }
                }
                model.addAttribute("dirs", dirs);
                model.addAttribute("dirIds", "");
                // 生成认证凭证
                LoginTicket loginTicket = new LoginTicket();
                loginTicket.setUserId(share_md5);
                loginTicket.setTicket(MD5Util.encodePwd(loginUser.getId() + System.currentTimeMillis()));
                loginTicket.setExpired(String.valueOf(System.currentTimeMillis() + 3600 * 12 * 1000));
                loginTicketService.insertLoginTicket(loginTicket);
                Cookie cookie = new Cookie("shareTicket", loginTicket.getTicket());
                cookie.setPath("/share/");
                cookie.setMaxAge(3600 * 24 * 100 * 1000);
                response.addCookie(cookie);
                return "user/share";
            }
        }
        // 判断是否拥有凭证
        if(!shareTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(shareTicket);
            // 查询凭证是否有效
            if (loginTicket != null && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()) {
                // 查询凭证和要访问的链接是否符合
                if (loginTicket.getUserId().equals(share_md5)) {
                    // 符合条件，跳转到分享内容页面
                    List<Dir> dirs = new ArrayList<>();
                    for (String dirId : share.getDir_ids().split(" ")){
                        Dir subDir = dirDao.getDirInfoById(dirId);
                        if(!subDir.getIsDir().equals("")) {
                            fileService.addDirFileType(subDir);
                            dirs.add(subDir);
                        }
                    }
                    model.addAttribute("dirIds", "");
                    model.addAttribute("dirs", dirs);
                    return "user/share";
                }
            }
        }
        if(loginAdmin != null){
            List<Dir> dirs = new ArrayList<>();
            for(String dirId: share.getDir_ids().split(" ")){
                Dir subDir = dirDao.getDirInfoById(dirId);
                if(!subDir.getIsDir().equals("")) {
                    fileService.addDirFileType(subDir);
                    dirs.add(subDir);
                }
            }
            model.addAttribute("dirs", dirs);
            model.addAttribute("dirIds", "");
            // 生成认证凭证
            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setUserId(share_md5);
            loginTicket.setTicket(MD5Util.encodePwd(loginAdmin.getId() + System.currentTimeMillis()));
            loginTicket.setExpired(String.valueOf(System.currentTimeMillis() + 3600 * 12 * 1000));
            loginTicketService.insertLoginTicket(loginTicket);
            Cookie cookie = new Cookie("shareTicket", loginTicket.getTicket());
            cookie.setPath("/share/");
            cookie.setMaxAge(3600 * 24 * 100 * 1000);
            response.addCookie(cookie);
            return "user/share";
        }
        // 链接没有失效且没有凭证，正常跳转到分享认证页面
        model.addAttribute("code", "unexpired");
        return "user/shareVerify";
    }

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
            , HttpServletResponse response){
        // 查询分享内容信息
        if(share_md5 == null){
            model.addAttribute("code", "expired");
            return "user/shareVerify";
        }
        Share share = shareDao.getShareInfoById(share_md5);
        // 分享是否存在
        if(share == null)
            return "error";
        // 链接是否被管理员设置为失效
        if(share.getStatus().equals("false")){
            model.addAttribute("code", "expired");
            return "user/shareVerify";
        }
        // 判断链接时间不是永久
        if(!share.getExpired().equals("forever")) {
            if (Long.valueOf(share.getExpired()) < System.currentTimeMillis()){
                // 链接失效直接返回认证页面（提示链接已失效）
                model.addAttribute("code", "expired");
                return "user/shareVerify";
            }
        }
        model.addAttribute("shareUserId", share.getUser_id());
        model.addAttribute("share_md5", share_md5);
        // 输入的口令是否正确
        if(share.getKey().equals(key)){
            List<Dir> dirs = new ArrayList<>();
            for(String dirId: share.getDir_ids().split(" ")){
                Dir subDir = dirDao.getDirInfoById(dirId);
                if(!subDir.getIsDir().equals("")) {
                    fileService.addDirFileType(subDir);
                    dirs.add(subDir);
                }
            }
            model.addAttribute("dirs", dirs);
            model.addAttribute("dirIds", "");
            // 生成认证凭证
            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setUserId(share_md5);
            loginTicket.setTicket(MD5Util.encodePwd(key + System.currentTimeMillis()));
            loginTicket.setExpired(String.valueOf(System.currentTimeMillis() + 3600 * 12 * 1000));
            loginTicketService.insertLoginTicket(loginTicket);
            Cookie cookie = new Cookie("shareTicket", loginTicket.getTicket());
            cookie.setPath("/share/");
            cookie.setMaxAge(3600 * 12 * 1000);
            response.addCookie(cookie);
            // 生成并添加分析信息
            // 分享被打开次数
            Analysis analysis = new Analysis();
            String date = DateUtil.longToString("yyyy-MM-dd", System.currentTimeMillis());
            analysis.setRowKey("share_open_" + share_md5 + "_" + date);
            analysis.setCount(analysisDao.getAnalysisCount(analysis.getRowKey()));
            analysisDao.addAnalysis(analysis);
            return "user/share";
        }
        else
            model.addAttribute("type", "口令输入错误，请重新输入");
        // 链接没有失效且没有凭证，正常跳转到分享认证页面
        model.addAttribute("code", "unexpired");
        return "user/shareVerify";
    }

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
            , @CookieValue(value = "shareTicket", defaultValue = "null") String shareTicket){
        // 查询分享内容信息
        Share share = shareDao.getShareInfoById(share_md5);
        // 分享是否存在
        if(share == null)
            return "error";
        // 链接是否被管理员设置为失效
        if(share.getStatus().equals("false")){
            model.addAttribute("code", "expired");
            return "user/shareVerify";
        }
        // 判断链接时间不是永久
        if(!share.getExpired().equals("forever")) {
            if (Long.valueOf(share.getExpired()) < System.currentTimeMillis()){
                // 链接失效直接返回认证页面（提示链接已失效）
                model.addAttribute("code", "expired");
                return "user/shareVerify";
            }
        }
        model.addAttribute("shareUserId", share.getUser_id());
        model.addAttribute("share_md5", share_md5);
        if(!shareTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(shareTicket);
            if (loginTicket != null && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()) {
                if (loginTicket.getUserId().equals(share_md5)) {
                    // 是否存在认证凭证、凭证是否已失效、凭证是否对应
                    List<Dir> dirs = new ArrayList<>();
                    if (dirIds.equals("")) {
                        for (String dirId : share.getDir_ids().split(" ")) {
                            Dir subDir = dirDao.getDirInfoById(dirId);
                            if(!subDir.getIsDir().equals("")) {
                                fileService.addDirFileType(subDir);
                                dirs.add(subDir);
                            }
                        }
                    } else if(dirIds.equals("null")) {
                        dirs = null;
                    } else {
                        for (String dirId : dirIds.split(" ")) {
                            Dir subDir = dirDao.getDirInfoById(dirId);
                            if(!subDir.getIsDir().equals("")) {
                                fileService.addDirFileType(subDir);
                                dirs.add(subDir);
                            }
                        }
                    }
                    model.addAttribute("parentId", parentId);
                    model.addAttribute("dirIds", dirIds);
                    model.addAttribute("dirs", dirs);
                    return "user/share";
                }
            }
        }
        // 链接没有失效且没有凭证，正常跳转到分享认证页面
        model.addAttribute("code", "unexpired");
        return "user/shareVerify";
    }

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
            , @CookieValue(value = "shareTicket", defaultValue = "null") String shareTicket){
        // 查询分享内容信息
        Share share = shareDao.getShareInfoById(share_md5);
        // 分享是否存在
        if(share == null)
            return "error";
        // 链接是否被管理员设置为失效
        if(share.getStatus().equals("false")){
            model.addAttribute("code", "expired");
            return "user/shareVerify";
        }
        // 判断链接时间不是永久
        if(!share.getExpired().equals("forever")) {
            if (Long.valueOf(share.getExpired()) < System.currentTimeMillis()){
                // 链接失效直接返回认证页面（提示链接已失效）
                model.addAttribute("code", "expired");
                return "user/shareVerify";
            }
        }
        model.addAttribute("shareUserId", share.getUser_id());
        model.addAttribute("share_md5", share_md5);
        if(!shareTicket.equals("null")) {
            LoginTicket loginTicket = userService.fineLoginTicket(shareTicket);
            if (loginTicket != null && Long.valueOf(loginTicket.getExpired()) > System.currentTimeMillis()) {
                if (loginTicket.getUserId().equals(share_md5)) {
                    // 是否存在认证凭证、凭证是否已失效、凭证是否对应
                    // 判断上一级是否是分享内容的根目录
                    for (String Id : share.getDir_ids().split(" ")) {
                        if (parentId.equals(Id)) {
                            dirIds = "";
                            break;
                        }
                    }
                    List<Dir> dirs = new ArrayList<>();
                    if (dirIds.equals("")) {
                        parentId = "";
                        for (String Id : share.getDir_ids().split(" ")){
                            Dir subDir = dirDao.getDirInfoById(Id);
                            if(!subDir.getIsDir().equals("")) {
                                fileService.addDirFileType(subDir);
                                dirs.add(subDir);
                            }
                        }
                    } else {
                        parentId = dirDao.getDirInfoById(parentId).getParent_id();
                        dirIds = dirDao.getDirInfoById(parentId).getSub_ids();
                        for (String Id : dirIds.split(" ")){
                            Dir subDir = dirDao.getDirInfoById(Id);
                            if(!subDir.getIsDir().equals("")) {
                                fileService.addDirFileType(subDir);
                                dirs.add(subDir);
                            }
                        }
                    }
                    model.addAttribute("parentId", parentId);
                    model.addAttribute("dirIds", dirIds);
                    model.addAttribute("dirs", dirs);
                    return "user/share";
                }
            }
        }
        // 链接没有失效且没有凭证，正常跳转到分享认证页面
        model.addAttribute("code", "unexpired");
        return "user/shareVerify";
    }
}
