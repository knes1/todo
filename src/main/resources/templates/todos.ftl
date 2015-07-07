<#-- @ftlvariable name="t" type="io.github.knes1.todo.model.Todo" -->
<#-- @ftlvariable name="todos" type="java.util.Collection" -->
<#import '_main.ftl' as m>
<@m.page title="Todo List">
<div class="container">
    <div class="row">
        <div class="eight columns" style="margin-top: 30px">
            <h1>Todo List</h1>
            <form action="todos" method="POST">
                <input type="text" name="description" placeholder="Enter new todo here">
                <button type="submit" class="button-primary">Create!</button>
            </form>
            <table style="width: 100%">
                <thead>
                    <tr>
                        <th>Todo</th>
                        <th>Created</th>
                        <th>Completed</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <#list todos as t>
                    <tr>
                        <td>${t.description?html}</td>
                        <td>${t.dateCreated}</td>
                        <td>${t.completed?string("yes", "no")}</td>
                        <td>
                            <#if !t.completed>
                                <a href="todos/${t.id?c}/completed">Done!</a>
                            </#if>
                            <a href="todos/${t.id?c}/delete">Delete</a>
                        </td>
                    </tr>
                    </#list>
                </tbody>
            </table>
            <#if !todos?has_content>
            <div>
                Nothing to do! Yea! Go create a new todo up there.
            </div>
            </#if>
        </div>
    </div>
</div>
</@m.page>