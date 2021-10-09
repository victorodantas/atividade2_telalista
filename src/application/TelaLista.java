/*Atividade 2
        Aplique o princípio da única responsabilidade para:
        Refatorar o método btnDownloadMouseClicked na classe sd_ThorEnt/Peer/src/java/aplicacao/TelaLista.java
        O método vai da linha 224 até a linha 394.
        Enviar link de seu repositório clonado.*/

package application;

import java.util.ArrayList;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelaLista extends javax.swing.JPanel {
    public TelaLog telaLog = new TelaLog();

    private void btnDownloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDownloadMouseClicked
        int linha = tblListaDeArquivos.getSelectedRow();
        String nomeArquivo = tblListaDeArquivos.getModel().getValueAt(linha, 0).toString();
        Arquivo arquivo = new Arquivo();
        for (int i = 0; i < arquivos.size(); i++) {
            if (arquivos.get(i).getNome().equals(nomeArquivo)) {
                arquivo = arquivos.get(i);
            }
        }


        //PREPARANDO PEERS PARA CONEXAO
        int tamanho_vetor = 200;//arquivo.getTamanhoVetor();
        int numero_peers = arquivo.getPeer().size();
        int l = 0;
        int[] vetor_principal = new int[tamanho_vetor];
        byte[] vetor_final;
        int tamanho_bloco = (int) tamanho_vetor / (numero_peers * 5);

        int progress = 0;
        barra.setMinimum(0);
        barra.setMaximum(tamanho_vetor - 1);
        barra.setValue(0);
        barra.setStringPainted(true);

        List<Thread> listaThreads = new ArrayList<>();

        List<PeerModelo> peers =

        for (int i = 0; i < vetor_principal.length; i++) {
            vetor_principal[i] = 200;
        }
        telaLog.logArea.append("fazendo download...\n");
        telaLog.logArea.append("Arquivo: " + arquivo.getNome());
        telaLog.logArea.append("Tamanho: " + arquivo.getTamanhoArquivo());

        for (int i = 0; i < vetor_principal.length; i++) {
            if (vetor_principal[i] < -128 || vetor_principal[i] > 127) {
                for (int j = 0; j < numero_peers; j++) {
                    if (peers.get(j).getDisponibilidade()) {
                        int ii = i;
                        i += tamanho_bloco;
                        int jj = j;
                        String hashArquivo = arquivo.getHashArquivo();
                        String nome = arquivo.getNome();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                threadRun(peers, jj, ii, tamanho_bloco, tamanho_vetor, vetor_principal);
                            }
                        });
                        listaThreads.add(thread);
                        thread.start();
                    } else {
                        l = j;
                        while (!peers.get(l).getDisponibilidade()) {
                            //System.out.println("peer off " + peers.get(l).getIp());
                            l++;
                            if (l >= peers.size()) {
                                //System.out.println("recomeça lista de peers...");
                                l = 0;
                            }
                        }
                        j = l - 1;
                    }
                }
                i--;
            }
            progress++;
            barra.setStringPainted(true);
            barra.setValue(progress);
            telaLog.logArea.append(barra.getString() + "\n");
        }


        //VERIFICANDO SE AS THREADS JA ENCERRARAM
        int indice = listaThreads.size();
        while (indice > 0) {
            for (int i = 0; i < listaThreads.size(); i++) {
                if (!listaThreads.get(i).isAlive()) {
                    listaThreads.remove(listaThreads.get(i));
                    indice--;
                    telaLog.logArea.append("Threads abertas: " + indice);
                    System.out.println("Threads abertas: " + indice);
                }
            }
        }
        barra.setStringPainted(false);
        telaLog.logArea.append("download feito!\n");
        System.out.println("download feito!\n");

        //SALVANDO ARQUIVO DO DOWNLOAD
        vetor_final = new byte[vetor_principal.length];

        for (int i = 0; i < vetor_principal.length; i++) {
            vetor_final[i] = (byte) vetor_principal[i];
        }
        telaLog.logArea.append("verificando...");
        System.out.println("verificando...");
        try {
            if (new TorrentFilesManage().getHashCode(vetor_final).equals(arquivo.getHashArquivo())) {
                new TorrentFilesManage().createFileFromByteArray("C://ThorEnt//" + arquivo.getNome(), vetor_final);
                System.out.println("ok");
                System.out.println("salvo!");
            } else {
                telaLog.logArea.append("Hash incorreto");
                telaLog.logArea.append("Hash esperado: " + arquivo.getHashArquivo());
                telaLog.logArea.append("Hash do arquivo baixado: " + new TorrentFilesManage().getHashCode(vetor_final));
                System.out.println("Hash incorreto");
                System.out.println("Hash esperado: " + arquivo.getHashArquivo());
                System.out.println("Hash do arquivo baixado: " + new TorrentFilesManage().getHashCode(vetor_final));
            }
            //new TorrentFilesManage().createFileFromByteArray("C://ThorEnt//testando.jpg", vetor_final);
        } catch (Exception ex) {
            telaLog.logArea.append("Salvar arquivo: " + ex.getMessage());
            System.out.println("Salvar arquivo: " + ex.getMessage());
            Logger.getLogger(TelaLista.class.getName()).log(Level.SEVERE, null, ex);
        }

        atualizar();

    }//GEN-LAST:event_btnDownloadMouseClicked


    public List<PeerModelo> buildPeerModelo(Arquivo arquivo){
        List<PeerModelo> peers = new ArrayList<>();

        for (int i = 0; i < arquivo.getPeer().size(); i++) {
            PeerModelo peer = new PeerModelo();
            peer.setIp(arquivo.getPeer().get(i));
            peer.setDisponibilidade(true);
            peers.add(peer);
        }
        return peers;
    }


    public void threadRun(List<PeerModelo> peers, int jj, int ii, int tamanho_bloco, int tamanho_vetor, int[] vetor_principal) {
        peers.get(jj).setDisponibilidade(false);
        int inicio_bloco = ii;
        telaLog.logArea.append("Peer escolhido " + peers.get(jj).getIp() + ": pacote " + inicio_bloco);
        System.out.println("Peer escolhido " + peers.get(jj).getIp() + ": pacote " + inicio_bloco);
        ArquivoDownload arquivoDownload = new ArquivoDownload();
        String url = "http://" + peers.get(jj).getIp() + ":8080/Peer/webresources/peer/download/" + tamanho_bloco + "/" + inicio_bloco + "/" + hashArquivo;
        try {
            String jsonDownload = new Conexao().conectaWebService(url, null, "GET");
            if (!jsonDownload.equals(null)) {
                arquivoDownload = new Gson().fromJson(jsonDownload, ArquivoDownload.class);
                peers.get(jj).setDisponibilidade(true);

                byte[] vetor_menor = new byte[arquivoDownload.getVetor().length];
                vetor_menor = arquivoDownload.getVetor();
                String hash = new TorrentFilesManage().getHashCode(vetor_menor);
                if (hash.equals(arquivoDownload.getHash())) {
                    telaLog.logArea.append("hash vetor ok: pacote " + inicio_bloco);
                    System.out.println("hash vetor ok: pacote " + inicio_bloco);
                    for (int k = 0; k < vetor_menor.length; k++) {
                        if (inicio_bloco < tamanho_vetor) {
                            vetor_principal[inicio_bloco] = vetor_menor[k];
                            inicio_bloco++;
                        }
                    }
                    //i += vetor_menor.length;
                } else {
                    System.out.println("not");
                    for (int k = 0; k < vetor_menor.length; k++) {
                        if (inicio_bloco < tamanho_vetor) {
                            vetor_principal[inicio_bloco] = -200;
                            inicio_bloco++;
                        }
                    }
                    //i = inicio_bloco;
                }
            } else {
                peers.get(jj).setDisponibilidade(false);
            }
        } catch (JsonSyntaxException | NoSuchAlgorithmException erro) {
            System.out.println("Erro na thread: " + erro.getMessage());
        }
    }







}
