// ReceitaIA: tela que conversa com a API do Spring (/food e /generate)

const CATEGORIAS = {
  CEREAIS: "Cereais",
  TUBERCULOS: "Tubérculos",
  LEGUMINOSAS: "Leguminosas",
  VERDURAS: "Verduras",
  LEGUMES: "Legumes",
  FRUTAS: "Frutas",
  PROTEINAS: "Proteínas",
  GORDURAS: "Gorduras",
  DOCES: "Doces",
  BEBIDAS: "Bebidas",
  TEMPEROS: "Temperos",
};

const el = (id) => document.getElementById(id);
const form = el("form-alimento");
const campos = {
  id: el("campo-id"),
  nome: el("campo-nome"),
  categoria: el("campo-categoria"),
  quantidade: el("campo-quantidade"),
  validade: el("campo-validade"),
};

let alimentos = [];

// ---------- API ----------
async function api(caminho, opcoes = {}) {
  const resp = await fetch(caminho, {
    headers: { "Content-Type": "application/json" },
    ...opcoes,
  });
  if (!resp.ok) {
    const texto = await resp.text();
    throw new Error(`Erro ${resp.status}${texto && texto.length < 200 ? ": " + texto : ""}`);
  }
  const tipo = resp.headers.get("content-type") || "";
  if (resp.status === 204) return null;
  return tipo.includes("application/json") ? resp.json() : resp.text();
}

// ---------- Datas ----------
function diasAte(dataISO) {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  const [a, m, d] = dataISO.split("-").map(Number);
  const alvo = new Date(a, m - 1, d);
  return Math.round((alvo - hoje) / 86400000);
}

function textoValidade(dias) {
  if (dias < 0) return dias === -1 ? "venceu ontem" : `venceu há ${-dias} dias`;
  if (dias === 0) return "vence hoje";
  if (dias === 1) return "vence amanhã";
  return `vence em ${dias} dias`;
}

function classeValidade(dias) {
  if (dias < 0) return "vencido";
  if (dias <= 7) return "logo";
  return "";
}

function dataBR(dataISO) {
  const [a, m, d] = dataISO.split("-");
  return `${d}/${m}/${a}`;
}

// ---------- Avisos ----------
function avisar(msg, erro = false) {
  const aviso = el("aviso");
  aviso.textContent = msg;
  aviso.classList.toggle("erro", erro);
}

// ---------- Despensa ----------
async function carregar() {
  try {
    alimentos = await api("/food");
    desenhar();
  } catch (e) {
    avisar(`Não foi possível carregar a despensa. Confira se o Spring está rodando. (${e.message})`, true);
  }
}

function desenhar() {
  const area = el("prateleiras");
  area.innerHTML = "";

  const total = alimentos.length;
  el("contador").textContent = total === 1 ? "1 alimento" : `${total} alimentos`;

  if (total === 0) {
    const vazio = document.createElement("p");
    vazio.className = "vazio";
    vazio.textContent = "A despensa está vazia. Adicione o primeiro alimento no formulário acima.";
    area.append(vazio);
    return;
  }

  for (const [chave, nomeCategoria] of Object.entries(CATEGORIAS)) {
    const daCategoria = alimentos
      .filter((a) => a.categoria === chave)
      .sort((a, b) => a.validade.localeCompare(b.validade));
    if (daCategoria.length === 0) continue;

    const bloco = document.createElement("div");
    bloco.className = "prateleira";
    const titulo = document.createElement("h3");
    titulo.textContent = nomeCategoria;
    const lista = document.createElement("ul");
    lista.className = "itens";

    daCategoria.forEach((a) => lista.append(criarItem(a)));
    bloco.append(titulo, lista);
    area.append(bloco);
  }
}

function criarItem(a) {
  const dias = diasAte(a.validade);
  const li = document.createElement("li");
  li.className = "item";

  const nome = document.createElement("span");
  nome.className = "item-nome";
  nome.textContent = a.nome;

  const info = document.createElement("span");
  info.className = "item-info";
  const qtd = document.createElement("span");
  qtd.textContent = `Quantidade: ${a.quantidade}`;
  const val = document.createElement("span");
  val.className = `validade ${classeValidade(dias)}`;
  val.textContent = textoValidade(dias);
  val.title = `Validade: ${dataBR(a.validade)}`;
  info.append(qtd, val);

  const acoes = document.createElement("span");
  acoes.className = "item-acoes";
  const editar = document.createElement("button");
  editar.type = "button";
  editar.className = "btn btn-texto";
  editar.textContent = "Editar";
  editar.setAttribute("aria-label", `Editar ${a.nome}`);
  editar.addEventListener("click", () => iniciarEdicao(a));

  const remover = document.createElement("button");
  remover.type = "button";
  remover.className = "btn btn-texto btn-perigo";
  remover.textContent = "Remover";
  remover.setAttribute("aria-label", `Remover ${a.nome}`);
  remover.addEventListener("click", () => pedirRemocao(remover, a));

  acoes.append(editar, remover);
  li.append(nome, acoes, info);
  return li;
}

// Primeiro clique pede confirmação; o segundo remove.
function pedirRemocao(botao, a) {
  if (!botao.classList.contains("confirmar")) {
    botao.classList.add("confirmar");
    botao.textContent = "Confirmar remoção";
    setTimeout(() => {
      if (botao.isConnected) {
        botao.classList.remove("confirmar");
        botao.textContent = "Remover";
      }
    }, 4000);
    return;
  }
  remover(a);
}

async function remover(a) {
  try {
    await api(`/food/${a.id}`, { method: "DELETE" });
    avisar(`${a.nome} removido(a) da despensa.`);
    if (campos.id.value === String(a.id)) cancelarEdicao();
    await carregar();
  } catch (e) {
    avisar(`Não foi possível remover ${a.nome}. ${e.message}`, true);
  }
}

// ---------- Formulário ----------
function iniciarEdicao(a) {
  campos.id.value = a.id;
  campos.nome.value = a.nome;
  campos.categoria.value = a.categoria;
  campos.quantidade.value = a.quantidade;
  campos.validade.value = a.validade;
  el("btn-salvar").textContent = "Salvar alterações";
  el("btn-cancelar").hidden = false;
  form.classList.add("editando");
  avisar(`Editando ${a.nome}.`);
  form.scrollIntoView({ behavior: "smooth", block: "start" });
  campos.nome.focus({ preventScroll: true });
}

function cancelarEdicao() {
  form.reset();
  campos.id.value = "";
  campos.quantidade.value = 1;
  campos.validade.value = dataDaquiA(7);
  el("btn-salvar").textContent = "Adicionar à despensa";
  el("btn-cancelar").hidden = true;
  form.classList.remove("editando");
}

function dataDaquiA(dias) {
  const d = new Date();
  d.setDate(d.getDate() + dias);
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

form.addEventListener("submit", async (ev) => {
  ev.preventDefault();
  const dto = {
    nome: campos.nome.value.trim(),
    categoria: campos.categoria.value,
    quantidade: Number(campos.quantidade.value),
    validade: campos.validade.value,
  };
  const id = campos.id.value;
  const botao = el("btn-salvar");
  botao.disabled = true;

  try {
    if (id) {
      await api(`/food/${id}`, { method: "PUT", body: JSON.stringify(dto) });
      avisar(`${dto.nome} atualizado(a).`);
    } else {
      await api("/food", { method: "POST", body: JSON.stringify(dto) });
      avisar(`${dto.nome} adicionado(a) à despensa.`);
    }
    cancelarEdicao();
    await carregar();
    campos.nome.focus();
  } catch (e) {
    avisar(`Não foi possível salvar. ${e.message}`, true);
  } finally {
    botao.disabled = false;
  }
});

el("btn-cancelar").addEventListener("click", () => {
  cancelarEdicao();
  avisar("");
});

// ---------- Receita ----------
el("btn-gerar").addEventListener("click", async () => {
  const botao = el("btn-gerar");
  const status = el("receita-status");
  const texto = el("receita-texto");

  if (alimentos.length === 0) {
    status.className = "receita-status erro";
    status.textContent = "Adicione alimentos na despensa antes de gerar uma receita.";
    return;
  }

  botao.disabled = true;
  texto.innerHTML = "";
  status.className = "receita-status";
  const inicio = Date.now();
  const atualizar = () => {
    const seg = Math.round((Date.now() - inicio) / 1000);
    status.innerHTML = `<span class="panela" aria-hidden="true"></span>O chef está cozinhando a ideia… ${seg}s`;
  };
  atualizar();
  const relogio = setInterval(atualizar, 1000);

  try {
    const receita = await api("/generate");
    const seg = Math.round((Date.now() - inicio) / 1000);
    status.textContent = `Receita pronta em ${seg}s.`;
    texto.innerHTML = DOMPurify.sanitize(marked.parse(String(receita)));
    botao.textContent = "Gerar outra receita";
  } catch (e) {
    status.className = "receita-status erro";
    status.textContent = e.message.includes("503")
      ? "O chef demorou demais para responder. Tente de novo em instantes."
      : `Não foi possível gerar a receita. ${e.message}`;
  } finally {
    clearInterval(relogio);
    botao.disabled = false;
  }
});

// ---------- Início ----------
for (const [valor, nome] of Object.entries(CATEGORIAS)) {
  campos.categoria.append(new Option(nome, valor));
}
campos.validade.value = dataDaquiA(7);
carregar();
