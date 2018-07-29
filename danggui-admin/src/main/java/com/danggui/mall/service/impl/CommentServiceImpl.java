package com.danggui.mall.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danggui.common.utils.Base64;
import com.danggui.mall.dao.CommentDao;
import com.danggui.mall.dao.CommentPictureDao;
import com.danggui.mall.entity.CommentEntity;
import com.danggui.mall.service.CommentService;

/**
 * 用户评价Service实现类
 *
 * @author GongXingSheng
 * 
 * @date 2017-08-28 17:03:40
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private CommentPictureDao commentPictureDao;

    @Override
    public CommentEntity queryObject(Integer id) {
        return commentDao.queryObject(id);
    }

    @Override
    public List<CommentEntity> queryList(Map<String, Object> map) {
        List<CommentEntity> commentEntities = commentDao.queryList(map);
        if (null != commentEntities && commentEntities.size() > 0) {
            for (CommentEntity commentEntity : commentEntities) {
                commentEntity.setContent(Base64.decode(commentEntity.getContent()));
            }
        }
        return commentEntities;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return commentDao.queryTotal(map);
    }

    @Override
    public int save(CommentEntity comment) {
        return commentDao.save(comment);
    }

    @Override
    public int update(CommentEntity comment) {
        return commentDao.update(comment);
    }

    @Override
    @Transactional
    public int delete(Integer id) {
        //删除评论同时删除评论的图片
        commentPictureDao.deleteByCommentId(id);
        return commentDao.delete(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return commentDao.deleteBatch(ids);
    }

    @Override
    public int toggleStatus(CommentEntity comment) {
        CommentEntity commentEntity = queryObject(comment.getId());
        if (commentEntity.getStatus() == 1) {
            commentEntity.setStatus(0);
        } else if (commentEntity.getStatus() == 0) {
            commentEntity.setStatus(1);
        }
        return commentDao.update(commentEntity);
    }
}