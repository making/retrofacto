# Retrofacto

A clone of [Postfacto](https://github.com/vmware-archive/postfacto)

<img width="1024" alt="image" src="https://github.com/making/retrofacto/assets/106908/bf054483-64f3-40e1-a23d-7e8bb9f3a312">

Demo (no backend)
https://retrofacto.maki.lol

## How to deploy to Tanzu Application Platform

```
tanzu service class-claim create retrofacto-db --class postgresql-unmanaged --parameter storageGB=1 -n apps

tanzu apps workload apply retrofacto \
  --app retrofacto \
  --git-repo https://github.com/making/retrofacto \
  --git-branch main \
  --type web \
  --annotation autoscaling.knative.dev/minScale=1 \
  --service-ref retrofacto-db=services.apps.tanzu.vmware.com/v1alpha1:ClassClaim:retrofacto-db \
  --build-env BP_JVM_VERSION=17 \
  -n apps
```
