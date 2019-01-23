//service层
app.service("brandService",function ($http) {

    //分页查询
    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page="+page+"&rows="+rows)
    };

    //新增
    this.add = function (entity) {
        return $http.post("../brand/add.do",entity);
    };

    //更新
    this.update =function (entity) {
        return $http.post("../brand/update.do",entity);
    };

    //根据id主键查询
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    };

    //批量删除
    this.delete = function (selectedIds) {
        return $http.get("../brand/delete.do?ids="+selectedIds);
    };

    //搜索
    this.search = function (searchEntity,page, rows) {
        return $http.post("../brand/search.do?page="+page+"&rows="+rows,searchEntity);
    };

});