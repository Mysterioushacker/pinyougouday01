app.controller("goodsController", function ($scope, $controller, $location, goodsService, uploadService,
                                            itemCatService, typeTemplateService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //加载列表数据
    $scope.findAll = function(){
        goodsService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

    $scope.save = function () {
        var object;

        //设置商品内容
        $scope.entity.goodsDesc.introduction=editor.html();

        if($scope.entity.goods.id != null){//更新
            object = goodsService.update($scope.entity);
        } else {//新增
            object = goodsService.add($scope.entity);
        }
        object.success(function (response) {
            if(response.success){
                alert(response.message);
                location.href = "goods.html";
            } else {
                alert(response.message);
            }
        });
    };

    $scope.findOne = function () {
        //获取路径中的商品id
        var id = $location.search()["id"];

        //如果是新增商品则直接返回
        if(id == null){
            return;
        }

        goodsService.findOne(id).success(function (response) {
            $scope.entity = response;

            //向富文本编辑器设置商品内容
            editor.html($scope.entity.goodsDesc.introduction);

            //转换商品图片列表
            $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
            //转换商品扩展属性
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
            //转换商品规格属性
            $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
            //商品SKU列表中的每一个SKU商品的spec转换为json对象
            if($scope.entity.itemList.length > 0){
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        });
    };

    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;
        }
        if(confirm("确定要删除已选择的记录吗")){
            goodsService.delete($scope.selectedIds).success(function (response) {
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
        goodsService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //上传商品图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if(response.success){
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传图片失败");
        });
    };

    //定义商品entity
    $scope.entity = {goods:{},goodsDesc:{itemImages:[]}};

    //保存商品到图片entity
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
        $scope.image_entity = {};
    };

    //删除商品图片
    $scope.delete_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    //读取一级商品分类列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        });
    };

    //读取二级商品分类列表
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.itemCat2List = response;
            });
        }
    });

    //读取三级商品分类列表
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.itemCat3List = response;
            });
        }
    });

    //当选择三级分类后，查询对应的分类模板id
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        if (newValue != undefined) {
            itemCatService.findOne(newValue).success(function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            });
        }
    });

    //根据模板id，查询其对应的品牌
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        if (newValue != undefined) {
            typeTemplateService.findOne(newValue).success(function (response) {
                $scope.typeTemplate = response;
                //将字符串转换为json对象
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

                //将分类模板对应的扩张属性设置给商品描述的扩展属性
                //商品描述的扩展属性将对应有值，具体结构形如：[{"text":"内存大小","value":"4G"},{"text":"颜色","value":"黑色"}]
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
            });

            //根据分类模板id查询其对应的规格及其规格的选项
            typeTemplateService.findSpecList(newValue).success(function (response) {
                $scope.specList = response;
            });
        }
    });

    //定义商品entity
    $scope.entity = {goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    //选择了具体规格选项后保存到商品描述中
    $scope.updateSpecAttributes = function ($event, specName, optionName) {
        //查看当前的规格是否已经选择过
        var specObj = $scope.findObjectByKeyAndValue($scope.entity.goodsDesc.specificationItems, "attributeName", specName);
        if(specObj != null) {
            if($event.target.checked){
                specObj.attributeValue.push(optionName);
            } else {
               var optIndex = specObj.attributeValue.indexOf(optionName);
                specObj.attributeValue.splice(optIndex, 1);

                //如果该规格没有任何选项了，那么也要删除该规格
                if(specObj.attributeValue.length == 0){
                    var specIndex = $scope.entity.goodsDesc.specificationItems.indexOf(specObj);
                    $scope.entity.goodsDesc.specificationItems.splice(specIndex, 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[optionName]});
        }
    };

    //每次点击了规格选项后生成最新的SKU列表
    $scope.createItemList = function () {
        //初始化
        $scope.entity.itemList = [{spec:{},price:0, num:9999, status:"0",isDefault:"0"}];

        for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i++) {
            var spec = $scope.entity.goodsDesc.specificationItems[i];
            $scope.entity.itemList = addColumn($scope.entity.itemList, spec.attributeName, spec.attributeValue);
        }
    };

    addColumn = function (itemList, specName, specOptions) {
        //最终返回的列表数据，也就是表格的数据
        var newItemList = [];

        for (var i = 0; i < itemList.length; i++) {
            var oldItem = itemList[i];
            for (var j = 0; j < specOptions.length; j++) {
                var option = specOptions[j];
                var newItem = JSON.parse(JSON.stringify(oldItem));
                newItem.spec[specName] = option;

                newItemList.push(newItem);
            }
        }
        return newItemList;
    };

    //商品的状态
    $scope.goodsStatus = ["未审核","审核中","审核通过","审核未通过","关闭"];

    //商品分类集合
    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                var itemCat = response[i];
                $scope.itemCatList[itemCat.id] = itemCat.name;
            }
        });
    };

    //判断规格选项是否已经存在选择的里面
    $scope.checkAttributeValue = function (specName, optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var spec = $scope.findObjectByKeyAndValue(items, "attributeName", specName);
        if(spec != null){
            if(spec.attributeValue.indexOf(optionName) >= 0){
                return true;
            }
        }
        return false;
    };

    //修改商品的状态
    $scope.updateStatus = function (status) {
        if($scope.selectedIds.length < 1) {
            alert("请先选择商品");
            return;
        }
        if(confirm("确定要更新选中的商品状态吗？")){
            goodsService.updateStatus($scope.selectedIds, status).success(function (response) {
                if(response.success) {
                    //刷新列表并清空选中的那些商品
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };
});