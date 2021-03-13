package com.tensquare.crawler.pipeline;

import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.IdWorker;

import javax.annotation.Resource;

@Slf4j
@Component
public class ArticleTxtPipeline implements Pipeline {

    @Resource
    private ArticleDao articleDao;

    @Resource
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
