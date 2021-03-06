package com.xhuicloud.upms.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xhuicloud.common.core.utils.R;
import com.xhuicloud.common.log.annotation.SysLog;
import com.xhuicloud.upms.entity.SysDept;
import com.xhuicloud.upms.service.SysDeptService;
import com.xhuicloud.upms.utils.TreeUtil;
import com.xhuicloud.upms.vo.DeptVo;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @program: XHuiCloud
 * @description: SysDeptController
 * @author: Sinda
 * @create: 2020-03-21 15:48
 */
@RestController
@RequestMapping("/dept")
@AllArgsConstructor
@Api(value = "dept", tags = "部门管理模块")
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 树形部门
     *
     * @return
     */
    @GetMapping(value = "/tree")
    public R getDeptTree() {
        return R.ok(TreeUtil.buildDeptTree(sysDeptService
                .list(Wrappers.<SysDept>lambdaQuery()
                        .orderByAsc(SysDept::getSort)), 0));
    }

    /**
     * 新增部门
     *
     * @param sysDept
     * @return
     */
    @SysLog("新增部门")
    @PostMapping
    @PreAuthorize("@authorize.hasPermission('sys_add_dept')")
    public R save(@Valid @RequestBody SysDept sysDept) {
        return R.ok(sysDeptService.saveDept(sysDept));
    }

    /**
     * 禁用，启用 部门
     *
     * @param id
     * @return
     */
    @SysLog("开启禁用部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("@authorize.hasPermission('sys_delete_dept')")
    public R delete(@PathVariable Integer id) {
        return R.ok(sysDeptService.deleteDept(id));
    }

    /**
     * 编辑部门
     *
     * @param sysDept
     * @return
     */
    @SysLog("编辑部门")
    @PutMapping
    @PreAuthorize("@authorize.hasPermission('sys_editor_dept')")
    public R update(@Valid @RequestBody SysDept sysDept) {
        return R.ok(sysDeptService.updateDept(sysDept));
    }

    /**
     * 补全部门树
     *
     * @return
     */
    @PostMapping(value = "/tree")
    public R getDeptTree(@RequestBody List<DeptVo> deptVos) {
        return R.ok(sysDeptService.getDeptTree(deptVos));
    }

}
