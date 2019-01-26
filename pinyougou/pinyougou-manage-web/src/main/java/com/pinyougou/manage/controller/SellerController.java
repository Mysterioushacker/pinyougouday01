package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seller")
@RestController
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return sellerService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
            //不设置也可以，设置默认为0，也就是未审核
            seller.setStatus("0");
            sellerService.add(seller);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSeller findOne(String id) {
        return sellerService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
            sellerService.update(seller);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(String[] ids) {
        try {
            sellerService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seller 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeller seller, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return sellerService.search(page, rows, seller);
    }

}
