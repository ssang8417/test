# Kubernetes 과제 - MongoDB 서비스를 위한 Manifest파일 작성
  - 현재 Kubernetes 클러스터 환경이 Virtual Box 툴로 Master Node 서버 1대와 Worker Node 서버 2대로 VM환경이 구축되어 있다.
  - Master Node에는 Kubectl설치되어 있고 명령을 통해 kubernetes 클러스트 환경을 제어할수 있는 환경이 구성되어 있다.
  - 현재 구성한 Kubernetes 클러스터 환경 기반 MongoDB서비스 제공을 위한 YAML파일을 작성한다.
 
## 사전 작업
  - NFS설치 및 구성
  ```
  모든 노드 서버 설치
  sudo apt-get update && sudo apt-get install nfs-kernel-server
  sudo apt-get update && sudo apt-get install nfs-common
  ---------------------------------------------------------
  
  Master Node에만 설정
  sudo mkdir -p /nfs/data-pv1/
  sudo chown nobody:nogroup /nfs/data-pv1/
  
  sudo vi /etc/exports
     ...
    /nfs/data-pv1/ 10.0.2.4(rw,sync,no_subtree_check) 10.0.2.5(rw,sync,no_subtree_check) 10.0.2.6(rw,sync,no_subtree_check)
    ...
    추가
    
  sudo systemctl restart nfs-kernel-server 또는 exportfs -a
  ---------------------------------------------------------
  ```

MongoDB 서비스를 위한 YAML파일 실행 순서를 아래와 같은 순서로 진행한다.

 ## kubernetes service 등록
  - kubernetes service를 NodePort type으로 등록하여 설정한 label를 사용하는 pod들에 대해 외부 접속 가능한 포트를 제공한다.
  - type: NodePort, selector: app=mongodb
  
  파일 명: mongo-svc.yaml
  ```
  apiVersion: v1
  kind: Service
  metadata:
    name: mongo-svc
  spec:
    type: NodePort
    ports:
    - port: 80
      targetPort: 27017
      nodePort: 30001
    selector:
      app: mongodb
  ```
  
  실행 방법
  ```
  $k create -f mongo-svc.yaml
  ```
  
  설치 확인
  ```
  $ k get svc -o wide | grep mongo-svc
  mongo-svc                NodePort    10.102.15.28    <none>        80:30001/TCP     19s    app=mongodb
  ```
  
## Kubernetes PV(PersistentVolume) 등록
  - 사전 작업으로 구성한 NFS를 이용한 PV환경 구성
  
  파일 명: nfs-pv1.yaml
  ```
  apiVersion: v1
  kind: PersistentVolume
  metadata:
    name: nfs-pv1
  spec:
    capacity:
      storage: 50Gi

  accessModes:
  - ReadWriteOnce
  - ReadOnlyMany

  persistentVolumeReclaimPolicy: Recycle

  nfs:
    server: 10.0.2.4
    path: /nfs/data-pv1/
  ```
  
  실행 방법
  ```
  $k create -f nfs-pv1.yaml
  ```
  
  설치 확인
  ```
  $k8s@node1:~/homework$ k get pv | grep nfs-pv1
  nfs-pv1   50Gi       RWO,ROX        Recycle          Available                                   63s
  ```

## Kubernetes PVC(PersistentVolumeClaim)등록
  - 등록한 PV를 사용하는 PVC등록
  
  파일 명: mongo-pvc.yaml
  ```
  apiVersion: v1
  kind: PersistentVolumeClaim
  metadata:
    name: mongodb-pvc
  spec:
    resources:
      requests:
        storage: 20Gi
    accessModes:
    - ReadWriteOnce
  ```
  
  실행 방법
  ```
  $k create -f mongo-pvc.yaml 
  ```
  
  설치 확인
  ```
  $k get pvc | grep mongodb-pvc
  mongodb-pvc   Bound    nfs-pv1   50Gi       RWO,ROX                       75s
  ```

## MongoDB서비스를 제공하는 Kubernetes Pod 생성
  - MongoDB서비스를 제공하는 Kubernetes Pod를 생성하는 YAML파일 작성
  
  파일 명: mongodb-pod.yaml
  ```
  apiVersion: v1
  kind: Pod
  metadata:
    name: mongodb-pod
    labels:
      app: mongodb
  spec:
    volumes:
    - name: dbdata
      persistentVolumeClaim:
        claimName: mongodb-pvc
    containers:
    - image: mongo
      name: mongodb-ctr
      command: ["mongod","--bind_ip","0.0.0.0"]
      volumeMounts:
      - name: dbdata
        mountPath: /data/db
      ports:
      - containerPort: 27017
        protocol: TCP
  ```
  
  실행 방법
  ```
  $k create -f mongodb-pod.yaml 
  ```
  
  설치 확인
  ```
  $k get po -o wide | grep mongodb-pod
  mongodb-pod              1/1     Running   0          21s     10.44.0.2    node3   <none>           <none>
  ```


## MongoDB 서비스 기동 확인 방법
  - MongoDB서비스가 정상적으로 작동하는지 확인하기 위한 테스트 방법
  - pod설치 시 yaml파일에서 command: ["mongod","--bind_ip","0.0.0.0"] bind_ip옵션을 설정해야 외부 ip에서 해당 서버로 접근 가능(보안)
  ##Mongodb-client를 이용한 접근
  ```
    #mongodb client설치
    sudo apt install mongodb-clients
    
    #mongodb client설치 완료 후
    mongo --host node1 --port 30001
    mongo --host node2 --port 30001
    mongo --host node3 --port 30001
    
    각 노드들에 대해서 NodePort로 open한 port로 접근되는지 확인
    
    cluster IP와 cluster Port로 접근되는지 확인
    mongo --host 10.98.107.162 --port 80
  ```
  
   ##Kubernetes exec명령어를 통한 접근
   ```
   $k exec -it mongodb-pod -- mongo
   ```
