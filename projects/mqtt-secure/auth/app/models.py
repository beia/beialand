from django.db import models


# Create your models here.
class MQTTUser(models.Model):
    username = models.CharField(max_length=30, unique=True)
    password = models.CharField(max_length=100)
    superuser = models.BooleanField(default=False)
    enabled = models.BooleanField()

    def __str__(self):
        return self.username

    class Meta:
        indexes = [
            models.Index(fields=['username']),
            models.Index(fields=['username', 'password']),
            models.Index(fields=['enabled'],
                         )
        ]


ACCESS_TYPE_CHOICES = [('sub', 'Subscribe'), ('pub', 'Publish'), ('both', 'Publish&Subscribe')]


class MQTTRule(models.Model):
    username = models.ForeignKey(MQTTUser, on_delete=models.CASCADE, db_index=True)
    topic_regex = models.CharField(max_length=100)
    access_type = models.CharField(max_length=4, choices=ACCESS_TYPE_CHOICES, db_index=True)
    # order = models.IntegerField(db_index=True)
