package com.tensquare.usercrawler.pipeline;

import com.tensquare.user.dao.UserDao;
import com.tensquare.usercrawler.pojo.User;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.DownloadUtil;
import util.IdWorker;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 入库类
 */
@Component
public class UserPipeline implements Pipeline {

    @Resource
    private IdWorker idWorker;

    @Resource
    private UserDao userDao;

    /**
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        User user = new User();
        user.setId(idWorker.nextId() + "");
        user.setNickname(resultItems.get("nickname"));
        String image = resultItems.get("image");//图片地址
        String fileName = image.substring(image.lastIndexOf("/") + 1);
        user.setAvatar(fileName);
        //userDao.save(user);
        //下载图片
        try {
            DownloadUtil.download(image, fileName, "e:/userimg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
