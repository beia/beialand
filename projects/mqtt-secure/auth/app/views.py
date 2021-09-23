from django.db.models import Q, F, Value, CharField
from django.http import HttpResponse
from django.utils.decorators import method_decorator
from django.views import View
from django.views.decorators.csrf import csrf_exempt

from .models import MQTTUser, MQTTRule


# Create your views here.

@method_decorator(csrf_exempt, name='dispatch')
class MQTTAuth(View):

    def post(self, request):
        requered_params = ('username', 'password')
        if not all([required in request.POST for required in requered_params]):
            # Bad request
            return HttpResponse(status=400)

        username = request.POST['username']
        password = request.POST['password']

        found = MQTTUser.objects.filter(username=username, password=password, enabled=True).count()
        if found:
            return HttpResponse(status=200)
        else:
            return HttpResponse(status=401)


@method_decorator(csrf_exempt, name='dispatch')
class MQTTSuperUser(View):
    def post(self, request):
        requered_params = ('username',)
        if not all([required in request.POST for required in requered_params]):
            # Bad request
            return HttpResponse(status=400)

        username = request.POST['username']

        found = MQTTUser.objects.filter(username=username, enabled=True, superuser=True).count()
        if found:
            return HttpResponse(status=200)
        else:
            return HttpResponse(status=401)


class MQTTAcl(View):
    def get(self, request):
        requered_params = ('access', 'username', 'clientid', 'ipaddr', 'topic')
        if not all([required in request.GET for required in requered_params]):
            # Bad request
            return HttpResponse(status=400)

        username = request.GET['username']
        topic = request.GET['topic']
        access = 'sub' if request.GET['access'] == '1' else 'pub'

        access_type_cond = Q(access_type=access) | Q(access_type='both')
        found = MQTTRule.objects.filter(username__username=username).annotate(
            topic=Value(topic, output_field=CharField())).filter(
            access_type_cond,
            username__username=username, topic__regex=F(r'topic_regex')).count()

        if found:
            return HttpResponse(status=200)
        else:
            return HttpResponse(status=403)

        return HttpResponse(status=403)
