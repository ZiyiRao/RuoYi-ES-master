package com.ruoyi.es.controller;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.es.bean.es.EsUserBean;
import com.ruoyi.es.bean.es.EsNginxBean;
import com.ruoyi.es.bean.mysql.UserBean;
import com.ruoyi.es.service.EsService;
import com.ruoyi.es.service.TsService;
import com.ruoyi.es.service.UserService;
import com.ruoyi.common.core.controller.BaseController;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

//import com.ruoyi.es.bean.es.Page;
import java.util.*;

/**
 * @author japhet_jiu
 * @version 1.0
 */
@Controller
@RequestMapping("/es")
public class EsController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    EsService esService;

    @Autowired
    TsService tsService;




    @GetMapping("/ips")
    public String Ips(){
        return "/es/ips";
    }
    @GetMapping("/nginx")
    public String Nginx(){
        return "/es/nginx";
    }
    @RequestMapping("/add")
    public String add(Model view){
        return "/es/add";
    }
    @RequestMapping("/update")
    public String update(Model view, String id){
        if(null == id){
            return "index";
        }
        view.addAttribute("id",id);
        return "/es/update";
    }

    @ResponseBody
    @PostMapping("/selectAllIps")
    public TableDataInfo selectAllIps(){
//        startPage();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsUserBean> resultList = new LinkedList();		//???????????????list

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()     //?????? ?????? ??????
                .size(100);                         //?????????size
//                .sort(new FieldSortBuilder("@timestamp")      //??????
//                .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("net-ips22-2020.08.13")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsUserBean t = JSON.parseObject(r, EsUserBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object = JSONArray.fromObject(resultList);

        return getDataTable(object);
    }


    @ResponseBody
    @PostMapping("/selectAllNginx")
    public TableDataInfo selectAllNginx(){
//        startPage();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsNginxBean> resultList = new LinkedList();		//???????????????list

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()     //?????? ?????? ??????
                .size(200);                         //?????????size
//                .sort(new FieldSortBuilder("@timestamp")      //??????
//                .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("nginx-client-2022.10")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsNginxBean t = JSON.parseObject(r, EsNginxBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object = JSONArray.fromObject(resultList);

        return getDataTable(object);
    }
    /**
     * ????????????
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        UserBean ub = new UserBean();
        ub.setId(Integer.parseInt(userId.toString()));
        mmap.put("user", userService.selectUserBeanList(ub).get(0));
        return "es/update";
    }

    /**
     * @PathVariable????????????Spring URI????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????URI???
     *
     * ???????????????????????????????????????@RequestParam?????????@PathVariable???
     *
//     * @param type
     * @param keyword
     * @return
     */
    @GetMapping("/searchIpsField/{field}/{keyword}")
    public String searchIpsField( @PathVariable("field")String field,@PathVariable("keyword")String keyword,ModelMap mmap){
        List<String> resultList = new LinkedList();
        resultList.add(field);
        resultList.add(keyword);
        JSONArray object = JSONArray.fromObject(resultList);
        System.out.println(object);
        mmap.put("list", object);
        return "es/search_ips";

    }

    @GetMapping("/searchNginxField/{field}/{keyword}")
    public String searchNginxField( @PathVariable("field")String field,@PathVariable("keyword")String keyword,ModelMap mmap){
        List<String> resultList = new LinkedList();
        resultList.add(field);
        resultList.add(keyword);
        JSONArray object = JSONArray.fromObject(resultList);
        System.out.println(object);
        mmap.put("list1", object);
        return "es/search_nginx";

    }

    @ResponseBody
    @PostMapping("/searchEsIps/{field}/{keyword}")
    public TableDataInfo searchEsIps(@PathVariable("field")String field,@PathVariable("keyword")String keyword) {
//        startPage();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsUserBean> resultList = new LinkedList();		//???????????????list

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()     //?????? ?????? ??????
                .size(20);                         //?????????size
//                .sort(new FieldSortBuilder("@timestamp")      //??????
//                .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("net-ips22-2020.08.13")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
                sourceBuilder.query(QueryBuilders.matchQuery(field,keyword));
//            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsUserBean t = JSON.parseObject(r, EsUserBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object = JSONArray.fromObject(resultList);

        return getDataTable(object);
    }

    @ResponseBody
    @PostMapping("/searchEsNginx/{field}/{keyword}")
    public TableDataInfo searchEsNginx(@PathVariable("field")String field,@PathVariable("keyword")String keyword) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsNginxBean> resultList = new LinkedList();		//???????????????list

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()     //?????? ?????? ??????
                .size(20);                         //?????????size
//                .sort(new FieldSortBuilder("@timestamp")      //??????
//                .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("nginx-client-2022.10")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
            sourceBuilder.query(QueryBuilders.matchQuery(field,keyword));
//            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsNginxBean t = JSON.parseObject(r, EsNginxBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object = JSONArray.fromObject(resultList);
        System.out.println(object);
        return getDataTable(object);
    }


    @ResponseBody
    @GetMapping("/searchAllIpsField")
    public List<EsUserBean> searchAllIpsField(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsUserBean> resultList = new ArrayList();		//???????????????list
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()      //?????? ?????? ??????
                .size(1000)                              //?????????size
                .sort(new FieldSortBuilder("@timestamp")      //??????
                        .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("net-ips22-2020.08.13")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsUserBean t = JSON.parseObject(r, EsUserBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object2 = JSONArray.fromObject(resultList);
        String str=JSON.toJSON(resultList).toString();
//      System.out.println(object2);
        return object2;
    }

    @ResponseBody
    @GetMapping("/searchAllNginxField")
    public List<EsNginxBean> searchAllNginxField(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        List<EsNginxBean> resultList = new ArrayList();		//???????????????list
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()      //?????? ?????? ??????
                .size(1000)                              //?????????size
                .sort(new FieldSortBuilder("@timestamp")      //??????
                        .order(SortOrder.DESC));                     //??????
        SearchRequest rq = new SearchRequest("nginx-client-2022.10")  //?????? ?????????
                .source(sourceBuilder);						 //??????????????????
        try {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            rq.source(sourceBuilder);
            SearchResponse resp = client.search(rq, RequestOptions.DEFAULT);
            for (SearchHit hit : resp.getHits()) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(hit.toString());
                String r = jsonObject.getString("_source");
                EsNginxBean t = JSON.parseObject(r, EsNginxBean.class);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                client.close();    //????????????
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONArray object = JSONArray.fromObject(resultList);
        String str=JSON.toJSON(resultList).toString();
//      System.out.println(object);
        return object;
    }



//    @GetMapping("/getMess/{id}")
//    @ResponseBody
//    public List<UserBean> getMess(@PathVariable("id") Integer id){
//        UserBean ub = new UserBean();
//        ub.setId(id);
//        List<UserBean> userBeans = userService.selectUserBeanList(ub);
//        return userBeans;
//    }
//
//    @PostMapping("/updateSave")
//    @ResponseBody
//    @Transactional(rollbackFor = Exception.class)
//    public AjaxResult updateSave(@RequestParam("id")String id, @RequestParam("name")String name, @RequestParam("age")String age, @RequestParam("phone")String phone, @RequestParam("remark")String remark){
//        //??????mysql?????????
//        UserBean ub = new UserBean();
//        ub.setId(Integer.parseInt(id));
//        ub.setName(name);
//        ub.setAge(Integer.parseInt(age));
//        ub.setPhone(phone);
//        ub.setRemark(remark);
//        int row = userService.updateById(ub);
//
//        //??????es
//        EsUserBean esUserBean = esService.queryEmployeeById(id);
//        esUserBean.setId(Integer.parseInt(id));
//        esUserBean.setName(name);
//        esUserBean.setAge(Integer.parseInt(age));
//        esUserBean.setPhone(phone);
//        esUserBean.setRemark(remark);
//        EsUserBean save = esService.save(esUserBean);
//        System.out.println(save);
//
//        /*BoolQueryBuilder builder = QueryBuilders.boolQuery();
//        builder.should(QueryBuilders.matchPhraseQuery("id",id));
//        Page<EsUserBean> search = (Page<EsUserBean>)esService.search(builder);
//        List<EsUserBean> content = search.getContent();
//        content.get(0).setId(Integer.parseInt(id));
//        content.get(0).setName(name);
//        content.get(0).setAge(Integer.parseInt(age));
//        content.get(0).setPhone(phone);
//        content.get(0).setRemark(remark);
//        esService.save(content.get(0));*/
//
//
//       /* Optional<EsUserBean> byId = esService.findById(Integer.parseInt(id));
//        byId.get().setId(Integer.parseInt(id));
//        byId.get().setName(name);
//        byId.get().setAge(Integer.parseInt(age));
//        byId.get().setPhone(phone);
//        byId.get().setRemark(remark);
//        esService.save(byId.get());*/
//
//
//        /*Iterable<EsUserBean> id1 = esService.search(new MatchQueryBuilder("id", id));
//        Iterator<EsUserBean> iterator = id1.iterator();
//        while (iterator.hasNext()){
//            EsUserBean next = iterator.next();
//            System.out.println(next);
//        }*/
//        if(row == 1 && save!=null){
//            return toAjax(row);
//        }else{
//            return toAjax(0);
//        }
//    }
//
//    @PostMapping("/addSave")
//    @ResponseBody
//    @Transactional(rollbackFor = Exception.class)
//    public AjaxResult addSave(UserBean ub){
//        //???mysql????????????
//        Date date = new Date();
//        Date date2 = new Date();
//        ub.setCreateTime(date);
//        ub.setUpdateTime(date2);
//        int row = userService.save(ub);
//
//
//        //???es????????????
//        EsUserBean eub = new EsUserBean();
//        eub.setId(ub.getId()); //??????es mysql ???id??????
//        eub.setName(ub.getName());
//        eub.setPassword(ub.getName());
//        eub.setAge(ub.getAge());
//        eub.setPhone(ub.getPhone());
//        eub.setRemark(ub.getRemark());
//        eub.setCreateTime(date);
//        eub.setUpdateTime(date2);
//        EsUserBean save = esService.save(eub);
//        if(row==1 && save!=null){
//            return toAjax(row);
//        }else{
//            return toAjax(0);
//        }
//    }
//
//
//    @PostMapping("/remove")
//    @ResponseBody
//    @Transactional(rollbackFor = Exception.class)
//    public AjaxResult deleteById(@RequestParam("ids") String id){
//        try {
//            //??????mysql ?????????
//            int row = userService.deleteById(id);
//            //??????es
//            EsUserBean esUserBean = esService.queryEmployeeById(id);
//            esService.delete(esUserBean);
//            if(row==1){
//                return toAjax(row);
//            }else{
//                return toAjax(0);
//            }
//        }catch (Exception e) {
//            return error(e.getMessage());
//        }
//
//    }

}