package com.tensquare.crawler.pipeline;

import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.IdWorker;

/**
 * 入库类-负责将爬取的数据存入数据库
 */
@Slf4j
@Component
public class ArticleDbPipeline implements Pipeline {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IdWorker idWorker;

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    private String channelId;//频道ID

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String content = resultItems.get("content");
        Article article = new Article();
        article.setId(idWorker.nextId() + "");
        article.setChannelid(channelId);
        article.setTitle(title);
        article.setContent(content);
        articleDao.save(article);
    }


}
