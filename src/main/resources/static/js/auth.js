// Trang sau khi đăng nhập thành công (đổi theo dự án của bạn)
const HOME_URL = 'index.html';
const $ = (id) => document.getElementById(id);

function showError(msg) { const e = $('formError'); e.textContent = msg; e.classList.remove('hidden'); }
function hideError() { $('formError').classList.add('hidden'); }

function bindPasswordToggle(inputId, btnId, iconId) {
  $(btnId).addEventListener('click', () => {
    const input = $(inputId), show = input.type === 'password';
    input.type = show ? 'text' : 'password';
    $(iconId).textContent = show ? 'visibility' : 'visibility_off';
  });
}

const API_BASE_URL = 'http://localhost:8080';

async function postJson(url, body) {
  const res = await fetch(API_BASE_URL + url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  });

  const text = await res.text();

  let data = null;
  try {
    data = JSON.parse(text);
  } catch (_) {}

  return {
    ok: res.ok,
    data,
    text
  };
}

function setBusy(btn, busy, label) {
  btn.disabled = busy;
  btn.querySelector('span').textContent = busy ? 'Đang xử lý...' : label;
}