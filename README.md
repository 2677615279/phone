##Shiro授权判断规则
shiro默认的权限资源标识符是英文的冒号，即":"，使用其命名的权限会被切割成多个级别放入set<T>中，
属于逻辑授权，Shiro以其内置的规则，自动区分权限上下级

命名重叠(即通过:切割后 存在重叠)的两个权限中，短名称的权限是级别更高的权限，
即simpleAuthorizationInfo只拥有高权限，那么低权限也默认拥有，即使低权限在simpleAuthorizationInfo中未授权不存在，例如：

simpleAuthorizationInfo中授权有且只有1个权限"*:*:*"，即使判断"users:*:*"是否授权，也是true，
simpleAuthorizationInfo中授权有且只有1个权限"users:*:*"，即使判断"users:insert:100"，也是true；但判断"*:*:*" ，是false

如果使用其他权限资源标识符，如"_"等，判断效率相对于冒号命名下降，因为变成了完全匹配，属于物理授权；
simpleAuthorizationInfo中没有的权限就是false，物理授权必须完全匹配，不考虑级别高低，只考虑simpleAuthorizationInfo中是否存在授权

##Shiro的rememberMe 记住我
参考ShiroConfig中的配置方法，配置Shiro记住我管理器，
当用户登录时勾选了记住我，只要用户不退出，无论关闭浏览器还是电脑，30天内(具体看ShiroConfig中自己配置的时长)，
再次访问ShiroConfig中配置的过滤级别为"anon"和"user"的url，都是不用登录的，也不会被ShiroFilter拦截过滤重定向登录页，前提是服务器一直运行

用法：
前台登录表单除principal、credentials、验证码外，再添加一个checkbox类型的input元素，name属性赋为rememberMe，
使用js将其属性值处理为boolean类型，选true 未选false；
将登录表单传来的boolean类型的rememberMe与principal、credentials一起传入AuthenticationToken的构造方法中，再执行登录

注意：
本项目中为前台普通用户的登录功能提供了可供选择的rememberMe，方便前台用户的体验及操作，不必因为关闭客户端而频繁的认证登录；
后台管理员的登录功能并没有提供rememberMe，管理员在后台的种种敏感操作，必须以执行登录为前提，另外还加了参数、角色、权限的校验
防止被他人使用管理员电脑利用rememberMe跳过登录直接进入后台进行敏感操作，从而提高管理员账户及后台敏感数据的安全性

##Shiro自定义RoleFilter 修改访问某url时的角色过滤规则-->由默认的与规则 转为 或规则
参考Shiro的RolesAuthorizationFilter，自定义RoleFilter，重写isAccessAllowed方法，修改角色过滤规则由默认的与关系改为或关系，
在ShiroConfig中将自定义RoleFilter加入Bean，再修改Bean为shiroFilterFactoryBean的方法，具体查看ShiroConfig

##Shiro设置登录失败次数限制 
限制用户登录尝试次数，防止坏人多次尝试，恶意暴力破解密码的情况出现，要限制用户登录尝试次数，必然要对账号密码验证失败做记录，
登录时，Shiro中账号密码的验证交给了CredentialsMatcher 所以在CredentialsMatcher里面检查，记录登录次数是最简单的做法。
当登录失败次数达到限制,修改数据库中的状态字段，并返回前台错误信息。

注意：
前台用户登录和后台管理员登录均适用。
当第一次锁定某用户完毕后，应及时清除登录失败次数缓存中该用户的数据；如果5分钟内管理员恢复该用户，不影响在此期间该用户后续的正常登录；
否则即使5分钟内管理员恢复该用户，在此期间该用户随即继续以正确的账号密码登录，
也会因为该用户登录失败次数缓存的数据依旧存在，被认为登录请求次数多于5次而导致被锁定。
