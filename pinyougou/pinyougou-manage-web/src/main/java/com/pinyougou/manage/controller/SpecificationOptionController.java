package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationOptionService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/specificationOption")
@RestController
public class SpecificationOptionController {

    @Reference
    private SpecificationOptionService specificationOptionService;

    @RequestMapping("/findAll")
    public List<TbSpecificationOption> findAll() {
        return specificationOptionService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return specificationOptionService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSpecificationOption specificationOption) {
        try {
            specificationOptionService.add(specificationOption);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSpecificationOption findOne(Long id) {
        return specificationOptionService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSpecificationOption specificationOption) {
        try {
            specificationOptionService.update(specificationOption);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            specificationOptionService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param specificationOption 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSpecificationOption specificationOption, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return specificationOptionService.search(page, rows, specificationOption);
    }

}
