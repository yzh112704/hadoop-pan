package com.example.d.dao;


import com.example.d.dao.basedao.HbaseDao;
import com.example.d.entity.User;
import com.example.d.util.Constants;
import com.example.d.util.UserUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository("userDao")
public class UserDao{
    @Autowired
    private HbaseDao hbaseDao;

    /**
     * 修改用户或添加用户基本信息
     * @param user
     */
    public void userInfo(User user){
        String[] value = {user.getPwd(), user.getName() , user.getSex(), user.getInstitute(), user.getGrade(), user.getMajor(), user.getDate(), user.getPhone(), user.getAddress(), user.getMail(), user.getStatus()};
        String rowKey = user.getId();
        hbaseDao.updateMoreData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_INFO, Constants.COLUMN_USER_INFO, value);
    }

    /**
     * 根据用户ID找到用户基本信息
     * @param id
     * @return
     */
    public User getUserInfoById(String id) {
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, id);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(id);
            user.setPwd(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[0]))));
            user.setName(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[1]))));
            user.setSex(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[2]))));
            user.setInstitute(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[3]))));
            user.setGrade(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[4]))));
            user.setMajor(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[5]))));
            user.setDate(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[6]))));
            user.setPhone(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[7]))));
            user.setAddress(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[8]))));
            user.setMail(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[9]))));
            user.setStatus(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[10]))));
            user.setTotalSpace(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[2]))));
        }
        return user;
    }

    /**
     * 删除用户所有信息
     * @param id
     */
    public void delUserInfoById(String id){
        hbaseDao.deleteDataByRow(Constants.TABLE_USER, id);
    }

    /**
     * 根据用户ID找到HostHolder用户的关键信息
     * @param userId
     * @return
     */
    public User getHostHolderUserById(String userId) {
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, userId);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(userId);
            user.setName(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_INFO), Bytes.toBytes(Constants.COLUMN_USER_INFO[1]))));
            user.setUse_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[0]))));
            user.setSurplus_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[1]))));
            user.setTotalSpace(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[2]))));
        }
        return user;
    }

    /**
     * 添加用户文件信息（文件ID与对应的数目）
     * @param user
     */
    public void addUserFile(User user){
        String rowKey = user.getId();
        String count = String.valueOf(Integer.valueOf(user.getFile_count()) + 1);
        hbaseDao.updateOneData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_FILE, user.getFile_id(), count);
    }

    /**
     * 获得用户文件数目
     * @param userId
     * @param fileId
     * @return
     */
    public String getUserFileCount(String userId, String fileId){
        String count = "";
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, userId);
        if(!result.isEmpty()) {
            count = Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_FILE), Bytes.toBytes(fileId)));
            if(count == null)
                count = "0";
        }
        return count;
    }

    /**
     * 删除用户文件信息（文件数目-1，为1时删除该条文件信息）
     * @param user
     */
    public void delUserFile(User user){
        String rowKey = user.getId();
        // 剩最后一个时，直接删除信息
        if(user.getFile_count().equals("1"))
            hbaseDao.deleteDataByColumn(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_FILE, user.getFile_id());
        else
        {
            String count = String.valueOf(Integer.valueOf(user.getFile_count()) - 1);
            hbaseDao.updateOneData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_FILE, user.getFile_id(), count);
        }
    }

    /**
     * 得到用户所有文件id、空间信息（用于计算用户剩余空间）
     * @param id
     * @return
     */
    public User getUserFilesAndSpace(String id){
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, id);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(id);
            user.setFile_ids(UserUtil.getColumnsByResult(result, Constants.FAMILY_USER_FILE));
            user.setUse_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[0]))));
            user.setSurplus_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[1]))));
            user.setTotalSpace(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[2]))));
        }
        return user;
    }

    /**
     * 添加用户文件夹信息
     * @param user
     */
    public void addUserDir(User user){
        String rowKey = user.getId();
        hbaseDao.updateOneData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_DIR, user.getDir_id(), user.getDir_id());
    }

    /**
     * 删除用户文件夹信息
     * @param user
     */
    public void delUserDir(User user){
        String rowKey = user.getId();
        hbaseDao.deleteDataByColumn(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_DIR, user.getDir_id());
    }

    /**
     * 得到用户所有文件夹id
     * @param userId
     * @return
     */
    public User getUserDirs(String userId){
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, userId);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(userId);
            user.setDir_ids(UserUtil.getColumnsByResult(result, Constants.FAMILY_USER_DIR));
        }
        return user;
    }

    /**
     * 添加用户分享信息
     * @param user
     */
    public void addUserShare(User user){
        String rowKey = user.getId();
        hbaseDao.updateOneData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_SHARE, user.getShare_md5(), "");
    }

    /**
     * 删除用户分享信息
     * @param user
     */
    public void delUserShare(User user){
        String rowKey = user.getId();
        hbaseDao.deleteDataByColumn(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_SHARE, user.getShare_md5());
    }

    /**
     * 得到用户所有分享链接
     * @param userId
     * @return
     */
    public User getUserShares(String userId){
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, userId);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(userId);
            user.setShare_md5s(UserUtil.getColumnsByResult(result, Constants.FAMILY_USER_SHARE));
        }
        return user;
    }

    /**
     * 添加用户空间信息
     * @param user
     */
    public void addUserSpace(User user){
        UserUtil.countSurplusSpace(user);
        String[] value = {user.getUse_space(), user.getSurplus_space(), user.getTotalSpace()};
        String rowKey = user.getId();
        hbaseDao.updateMoreData(Constants.TABLE_USER, rowKey, Constants.FAMILY_USER_SPACE, Constants.COLUMN_USER_SPACE, value);
    }

    /**
     * 只添加用户总空间信息
     * @param userId
     * @param totalSpace
     */
    public void addUserTotalSpace(String userId, String totalSpace){ hbaseDao.updateOneData(Constants.TABLE_USER, userId, Constants.FAMILY_USER_SPACE, Constants.COLUMN_USER_SPACE[2], totalSpace); }

    /**
     * 得到用户空间信息
     * @param userId
     * @return
     */
    public User getUserSpace(String userId){
        User user = null;
        Result result = hbaseDao.getResultByRow(Constants.TABLE_USER, userId);
        if(!result.isEmpty()) {
            user = new User();
            user.setId(userId);
            user.setUse_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[0]))));
            user.setSurplus_space(Bytes.toString(result.getValue(Bytes.toBytes(Constants.FAMILY_USER_SPACE), Bytes.toBytes(Constants.COLUMN_USER_SPACE[1]))));
        }
        return user;
    }
}
