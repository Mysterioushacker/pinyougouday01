app.controller("indexController", function ($scope, contentService) {

    $scope.contentList = [];

    //根据内容分类id查询内容列表
    $scope.findContentListByCategoryId = function (categoryId) {
        contentService.findContentListByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
        });
    };

    //根据搜索关键字跳转到搜索系统进行搜索
    $scope.search = function () {
        location.href= "http://search.pinyougou.com/search.html#?keywords=" + $scope.keywords;
    };
});