<template>
  <footer class="footer">
    <div class="footer__inner">
      <!-- Grid: 4 columns -->
      <div class="footer__grid">

        <!-- Col 1: Brand -->
        <div class="footer__col">
          <div class="footer__brand">
            <i class="fa-solid fa-car footer__brand-icon"></i>
            <span class="footer__brand-text">爱租车</span>
          </div>
          <p class="footer__desc">专业汽车租赁服务平台</p>
          <div class="footer__contact">
            <div class="footer__contact-item">
              <i class="fa-solid fa-phone footer__contact-icon"></i>
              <span>400-888-6688</span>
            </div>
            <div class="footer__contact-item">
              <i class="fa-solid fa-envelope footer__contact-icon"></i>
              <span>service@aizuche.com</span>
            </div>
          </div>
        </div>

        <!-- Col 2: 快速链接 -->
        <div class="footer__col">
          <h4 class="footer__heading">快速链接</h4>
          <ul class="footer__links">
            <li><a href="#/" class="footer__link">首页</a></li>
            <li><a href="#/" class="footer__link">服务特色</a></li>
            <li><a href="#/" class="footer__link">车型展示</a></li>
            <li><a href="#/" class="footer__link">租车流程</a></li>
          </ul>
        </div>

        <!-- Col 3: 服务支持 -->
        <div class="footer__col">
          <h4 class="footer__heading">服务支持</h4>
          <ul class="footer__links">
            <li><a href="#/" class="footer__link">常见问题</a></li>
            <li><a href="#/" class="footer__link">租车须知</a></li>
            <li><a href="#/" class="footer__link">保险说明</a></li>
            <li><a href="#/" class="footer__link">违章处理</a></li>
          </ul>
        </div>

        <!-- Col 4: 关于我们 -->
        <div class="footer__col">
          <h4 class="footer__heading">关于我们</h4>
          <ul class="footer__links">
            <li><a href="#/" class="footer__link">公司简介</a></li>
            <li><a href="#/" class="footer__link">联系方式</a></li>
            <li><a href="#/" class="footer__link">加入我们</a></li>
            <li><a href="#/" class="footer__link">用户协议</a></li>
          </ul>
        </div>

      </div>

      <!-- Bottom bar -->
      <div class="footer__bottom">
        <p class="footer__copyright">
          &copy; 2026 爱租车汽车租赁服务平台 &middot; 粤ICP备XXXXXXXX号
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
// FooterBar – no reactive logic required currently
</script>

<style scoped>
/* -----------------------------------------------
   Variables
----------------------------------------------- */
.footer {
  --footer-bg: #0f172a;
  --footer-text: #94a3b8;
  --footer-heading: #f1f5f9;
  --footer-link-hover: #ffffff;
  --footer-border: rgba(255, 255, 255, 0.08);
  --footer-bottom-bg: rgba(0, 0, 0, 0.25);
  --accent: #3b82f6;

  background: var(--footer-bg);
  color: var(--footer-text);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui,
    -apple-system, sans-serif;
  margin-top: auto;
}

/* -----------------------------------------------
   Inner container
----------------------------------------------- */
.footer__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 24px 0;
}

/* -----------------------------------------------
   4-column grid
----------------------------------------------- */
.footer__grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1fr;
  gap: 40px;
  padding-bottom: 40px;
}

/* -----------------------------------------------
   Col 1: Brand
----------------------------------------------- */
.footer__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.footer__brand-icon {
  font-size: 28px;
  color: var(--accent);
}

.footer__brand-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--footer-heading);
  letter-spacing: 0.5px;
}

.footer__desc {
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px;
  color: var(--footer-text);
}

.footer__contact {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer__contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--footer-text);
}

.footer__contact-icon {
  font-size: 14px;
  color: var(--accent);
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

/* -----------------------------------------------
   Col 2–4: Heading & links
----------------------------------------------- */
.footer__heading {
  font-size: 16px;
  font-weight: 600;
  color: var(--footer-heading);
  margin: 0 0 16px;
  position: relative;
  padding-bottom: 10px;
}

.footer__heading::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 28px;
  height: 2px;
  background: var(--accent);
  border-radius: 1px;
}

.footer__links {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer__link {
  text-decoration: none;
  color: var(--footer-text);
  font-size: 14px;
  transition: color 0.2s ease, padding-left 0.2s ease;
  display: inline-block;
  position: relative;
}

.footer__link:hover {
  color: var(--footer-link-hover);
  padding-left: 6px;
}

/* -----------------------------------------------
   Bottom bar
----------------------------------------------- */
.footer__bottom {
  border-top: 1px solid var(--footer-border);
  padding: 20px 0;
  text-align: center;
}

.footer__copyright {
  margin: 0;
  font-size: 13px;
  color: var(--footer-text);
}

/* -----------------------------------------------
   Responsive
----------------------------------------------- */
@media (max-width: 900px) {
  .footer__grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 540px) {
  .footer__inner {
    padding-top: 40px;
  }

  .footer__grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .footer__brand {
    justify-content: center;
  }

  .footer__desc {
    text-align: center;
  }

  .footer__contact {
    align-items: center;
  }

  .footer__col {
    text-align: center;
  }

  .footer__heading::after {
    left: 50%;
    transform: translateX(-50%);
  }

  .footer__links {
    align-items: center;
  }
}
</style>
