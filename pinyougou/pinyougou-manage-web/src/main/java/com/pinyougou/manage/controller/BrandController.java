package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        //return brandService.queryAll();
        return brandService.findAll();
    }

    /**
     * 分页查询
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1") Integer page, @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        //return brandService.testPage(page,rows);
        return brandService.findPage(page,rows);
    }

    /**
     * 保存商家信息(新增)
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return Result.ok("新增成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("新增失败");
    }

    /**
     * 根据主键id查询信息
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("修改成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 批量删除
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try{
            brandService.deleteByIds(ids);
            return Result.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     *
     * @param brand 查询条件
     * @param page  当前页号
     * @param rows  页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,@RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        return brandService.search(brand,page,rows);
    }

    /**
     * 查询品牌列表，返回的数据格式符合select2格式
     * @return
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){

        return brandService.selectOptionList();
    }

}
