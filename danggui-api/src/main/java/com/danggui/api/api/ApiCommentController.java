package com.danggui.api.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danggui.api.annotation.IgnoreAuth;
import com.danggui.api.annotation.LoginUser;
import com.danggui.api.entity.CommentPictureVo;
import com.danggui.api.entity.CommentVo;
import com.danggui.api.entity.CouponVo;
import com.danggui.api.entity.UserCouponVo;
import com.danggui.api.entity.UserVo;
import com.danggui.api.service.ApiCommentPictureService;
import com.danggui.api.service.ApiCommentService;
import com.danggui.api.service.ApiCouponService;
import com.danggui.api.service.ApiUserCouponService;
import com.danggui.api.service.ApiUserService;
import com.danggui.api.util.ApiBaseAction;
import com.danggui.api.util.ApiPageUtils;
import com.danggui.common.utils.Base64;
import com.danggui.common.utils.CharUtil;
import com.danggui.common.utils.Query;
import com.danggui.common.utils.StringUtils;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * 描述: ApiIndexController <br>
 */
@RestController
@RequestMapping("/api/comment")
public class ApiCommentController extends ApiBaseAction {
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiCommentPictureService commentPictureService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiUserCouponService apiUserCouponService;

    /**
     * 发表评论
     */
    @RequestMapping("post")
    public Object post(@LoginUser UserVo loginUser) {
        Map<String, CouponVo> resultObj = new HashMap<String, CouponVo>();
        //
        JSONObject jsonParam = getJsonRequest();
        Integer typeId = jsonParam.getInteger("typeId");
        Integer valueId = jsonParam.getInteger("valueId");
        String content = jsonParam.getString("content");
        JSONArray imagesList = jsonParam.getJSONArray("imagesList");
        CommentVo commentEntity = new CommentVo();
        commentEntity.setType_id(typeId);
        commentEntity.setValue_id(valueId);
        commentEntity.setContent(content);
        commentEntity.setStatus(0);
        //
        commentEntity.setAdd_time(System.currentTimeMillis() / 1000);
        commentEntity.setUser_id(loginUser.getUserId());
        commentEntity.setContent(Base64.encode(commentEntity.getContent()));
        Integer insertId = commentService.save(commentEntity);
        //
        if (insertId > 0 && null != imagesList && imagesList.size() > 0) {
            int i = 0;
            for (Object imgLink : imagesList) {
                i++;
                CommentPictureVo pictureVo = new CommentPictureVo();
                pictureVo.setComment_id(insertId);
                pictureVo.setPic_url(imgLink.toString());
                pictureVo.setSort_order(i);
                commentPictureService.save(pictureVo);
            }
        }
        // 是否领取优惠券
        if (insertId > 0 && typeId == 0) {
            // 当前评价的次数
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("value_id", valueId);
            List<CommentVo> commentVos = commentService.queryList(param);
            boolean hasComment = false;
            for (CommentVo commentVo : commentVos) {
                if (commentVo.getUser_id().equals(loginUser.getUserId())
                        && !commentVo.getId().equals(insertId)) {
                    hasComment = true;
                }
            }
            if (!hasComment) {
                Map<String, Object> couponParam = new HashMap<String, Object>();
                couponParam.put("send_type", 6);
                CouponVo newCouponConfig = apiCouponService.queryMaxUserEnableCoupon(couponParam);
                if (null != newCouponConfig
                        && newCouponConfig.getMin_transmit_num() >= commentVos.size()) {
                    UserCouponVo userCouponVo = new UserCouponVo();
                    userCouponVo.setAdd_time(new Date());
                    userCouponVo.setCoupon_id(newCouponConfig.getId());
                    userCouponVo.setCoupon_number(CharUtil.getRandomString(12));
                    userCouponVo.setUser_id(loginUser.getUserId());
                    apiUserCouponService.save(userCouponVo);
                    resultObj.put("coupon", newCouponConfig);
                }
            }
        }
        if (insertId > 0) {
            return toResponsObject(0, "评论添加成功", resultObj);
        } else {
            return toResponsFail("评论保存失败");
        }
    }

    /**
     */
    @RequestMapping("count")
    public Object count(@LoginUser UserVo loginUser, Integer typeId, Integer valueId) {
        Map<String, Object> resultObj = new HashMap<String, Object>();
        //
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type_id", typeId);
        param.put("value_id", valueId);
        Integer allCount = commentService.queryTotal(param);
        Integer hasPicCount = commentService.queryhasPicTotal(param);
        //
        resultObj.put("allCount", allCount);
        resultObj.put("hasPicCount", hasPicCount);
        return toResponsSuccess(resultObj);
    }

    /**
     * @param typeId
     * @param valueId
     * @param showType 选择评论的类型 0 全部， 1 只显示图片
     * @param page
     * @param size
     * @return
     */
    @IgnoreAuth
    @RequestMapping("list")
    public Object list(Integer typeId, Integer valueId, Integer showType,
                       @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                       String sort, String order) {
        Map<String, Object> resultObj = new HashMap<String, Object>();
        List<CommentVo> commentList = new ArrayList<CommentVo>();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type_id", typeId);
        param.put("value_id", valueId);
        param.put("page", page);
        param.put("limit", size);
        if (StringUtils.isNullOrEmpty(sort)) {
            param.put("order", "desc");
        } else {
            param.put("order", sort);
        }
        if (StringUtils.isNullOrEmpty(order)) {
            param.put("sidx", "id");
        } else {
            param.put("sidx", order);
        }
        if (null != showType && showType == 1) {
            param.put("hasPic", 1);
        }
        //查询列表数据
        Query query = new Query(param);
        commentList = commentService.queryList(query);
        int total = commentService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(commentList, total, query.getLimit(), query.getPage());
        //
        for (CommentVo commentItem : commentList) {
            commentItem.setContent(Base64.decode(commentItem.getContent()));
            commentItem.setUser_info(userService.queryObject(commentItem.getUser_id()));

            Map<String, Object> paramPicture = new HashMap<String, Object>();
            paramPicture.put("comment_id", commentItem.getId());
            List<CommentPictureVo> commentPictureEntities = commentPictureService.queryList(paramPicture);
            commentItem.setPic_list(commentPictureEntities);
        }
        return toResponsSuccess(pageUtil);
    }
}