app.controller("contentController", function ($scope, $controller, contentService, uploadService, contentCategoryService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        contentService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        contentService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;
        if($scope.entity.id != null){//更新
            object = contentService.update($scope.entity);
        } else {//新增
            object = contentService.add($scope.entity);
        }
        object.success(function (response) {
            if(response.success){
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };

    $scope.findOne = function (id) {
        contentService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            contentService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };

    $scope.searchEntity = {};//初始为空
    $scope.search = function (page, rows) {
        contentService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //内容状态
    $scope.contentStatus = ["无效","有效"];

    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if(response.success) {
                $scope.entity.pic = response.message;
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传失败");
        });
    };

    //查询内容分类列表
    $scope.findContentCategoryList = function () {
        contentCategoryService.findAll().success(function (response) {
            $scope.contentCategoryList = response;
        });
    };

});