from django.contrib import admin

from .models import MQTTUser, MQTTRule

admin.site.site_header = 'Interfata Administrare MQTT Secure'


# Register your models here.
@admin.register(MQTTUser)
class MQTTUsernameAdmin(admin.ModelAdmin):
    list_display = ('id', 'username', 'superuser', 'enabled')
    #list_editable = ('username', 'superuser', 'enabled')


@admin.register(MQTTRule)
class MQTTRuleAdmin(admin.ModelAdmin):
    list_display = ('id', 'username', 'topic_regex', 'access_type')
    list_editable = ('username', 'topic_regex', 'access_type')
