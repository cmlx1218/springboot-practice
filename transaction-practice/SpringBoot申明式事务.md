## SpringBoot申明式事务

#### 使用步骤
1. 启动类上开启事务支持（实践不需要加）  
&nbsp;`@EnableTransactionManagement`
2. 在业务逻辑层接口的实现中的相关方法上申明事务  
&nbsp;`@Transaction(propagation = Propagation.REQUIRED, readOnly = false)`

---

#### @Transaction注解的常用属性
* propagation: 事务的传播行为，默认值为REQUIRED
* isolation: 事务的隔离度，默认值采用DEFAULT
* timeout: 事务的超时时间，默认值为-1，不超时。如果超过了时间还没有完成事务，则自动回滚事务
* readOnly: 指定事务为只读事务，默认false
* rollbackFor: 用于指定能够触发事务回滚的异常类型，多个的话用逗号,分割
* noRollbackFor: 抛出指定的异常类型，不会滚事务

---

#### 注意事项
* Service实现类（一般不建议在接口上）添加@Transaction，可以将整个类纳入spring事务管理，在每个业务方法执行时都会开启一个事务，不过这些事务采用相同的管理方式
* 只能应用到public可见度上的方法，应用在其他上面不会报错，但是事务不会起作用
* 默认情况下，Transaction注解的事务所管理的方法中，如果抛出运行时异常或error，那么会进行事务回滚;如果抛出的非运行时异常就不会回滚。SQL异常属于检查异常，如果框架没有重写为运行时异常，事务对检查异常不会做出回滚  
&nbsp; **注**被catch处理了的异常，不会比事务作为判断依据;如果异常被catch了，但是又在catch中抛出了新的异常，那么事务会以这个新的异常作为是否回滚的判断依据

---

#### 事务的传播机制（行为）
&nbsp;事务的传播行为是指，同一个方法，在一个事务开始之前，另一个事务上下文已经存在，此时有若干选项可以指定一个事务性方法的只想方法  
&nbsp;在执行一个@Transaction注释的方法时，开启了事务。在该方法还在执行时，另一个人也触发了该方法  
* PROPAGATION_REQUIRED: 如果当前存在事务，则加入该事务；没有事务就创建一个新的事务，默认值
* PROPAGATION_REQUIRES_NEW: 创建一个新的事务，如果存在事务，则把该事务挂起
* PROPAGATION_SUPPORTS: 如果当前存在事务，则加入该事务; 如果当前没有事务，则以非事务方式继续运行
* PROPAGATION_NOT_SUPPORTED: 以非事务方式运行，如果存在事务，则把当前事务挂起
* PROPAGATION_NEVER: 以非事务方式运行，如果存在事务，则抛出异常
* PROPAGATION_MANDATORY: 如果当前存在事务，则加入该事务; 如果当前没有事务就抛出异常
* PROPAGATION_NESTED: 如果当前存在事务，则需要创建一个事务作为当前事务的嵌套事务来运行; 如果当前没有事务，则该取值等价于PROPAGATION_REQUIRED  

---

#### 注意
**1、同一个事务里面，对某一条数据的增删改，都会影响到这个事务里面接下来的对这条数据的增删改查**  
**2、事务场景中，抛出异常被catch之后，如果需要回滚，一定要手动回滚事务**
