package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/typeTemplate")
@RestController
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/findAll")
    public List<TbTypeTemplate> findAll() {
        return typeTemplateService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return typeTemplateService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbTypeTemplate typeTemplate) {
        try {
            typeTemplateService.add(typeTemplate);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbTypeTemplate typeTemplate) {
        try {
            typeTemplateService.update(typeTemplate);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            typeTemplateService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param typeTemplate 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbTypeTemplate typeTemplate, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return typeTemplateService.search(page, rows, typeTemplate);
    }

}
