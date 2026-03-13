const { createApp, ref } = Vue;
const { ElMessage } = ElementPlus;

const app = createApp({
    setup() {
        const loginFormRef = ref(null);
        const registerFormRef = ref(null);
        const isRegister = ref(false);

        const loginForm = ref({
            username: '',
            password: ''
        });

        const registerForm = ref({
            username: '',
            password: '',
            confirmPassword: ''
        });

        const loginRules = {
            username: [
                { required: true, message: '请输入用户名', trigger: 'blur' },
                { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入密码', trigger: 'blur' },
                { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
            ]
        };

        const registerRules = {
            username: [
                { required: true, message: '请输入用户名', trigger: 'blur' },
                { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入密码', trigger: 'blur' },
                { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
            ],
            confirmPassword: [
                { required: true, message: '请确认密码', trigger: 'blur' },
                {
                    validator: (rule, value, callback) => {
                        if (value !== registerForm.value.password) {
                            callback(new Error('两次输入的密码不一致'));
                        } else {
                            callback();
                        }
                    },
                    trigger: 'blur'
                }
            ]
        };

        const handleLogin = async () => {
            if (!loginFormRef.value) return;
            
            await loginFormRef.value.validate(async (valid) => {
                if (valid) {
                    try {
                        const response = await fetch('/api/login', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(loginForm.value)
                        });

                        if (response.ok) {
                            const data = await response.json();
                            localStorage.setItem('token', data.token);
                            window.location.href = 'index.html';
                        } else {
                            ElMessage.error('用户名或密码错误');
                        }
                    } catch (error) {
                        ElMessage.error('登录失败，请稍后重试');
                    }
                }
            });
        };

        const handleRegister = async () => {
            if (!registerFormRef.value) return;
            
            await registerFormRef.value.validate(async (valid) => {
                if (valid) {
                    try {
                        const response = await fetch('/api/auth/register', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                username: registerForm.value.username,
                                password: registerForm.value.password
                            })
                        });

                        if (response.ok) {
                            ElMessage.success('注册成功，请登录');
                            isRegister.value = false;
                            registerForm.value = {
                                username: '',
                                password: '',
                                confirmPassword: ''
                            };
                        } else {
                            const data = await response.json();
                            ElMessage.error(data.error || '注册失败，请稍后重试');
                        }
                    } catch (error) {
                        ElMessage.error('注册失败，请稍后重试');
                    }
                }
            });
        };

        return {
            loginFormRef,
            registerFormRef,
            loginForm,
            registerForm,
            loginRules,
            registerRules,
            isRegister,
            handleLogin,
            handleRegister
        };
    }
});

app.use(ElementPlus);
app.mount('#app'); 